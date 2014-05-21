package nl.marktplaats.twitmarkt;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import nl.marktplaats.common.remoting.RequestData;
import nl.marktplaats.shared.auth.remoting.AuthService;
import nl.marktplaats.shared.auth.remoting.MarktplaatsAccessToken;
import nl.marktplaats.twitmarkt.api.TweetToItemConverter;
import nl.marktplaats.twitmarkt.model.Link;
import nl.marktplaats.twitmarkt.persistence.LinkDao;
import nl.marktplaats.twitmarkt.twitter.TweetRetriever;
import nl.marktplaats.twitmarkt.twitter.TwitterUpdate;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import twitter4j.MediaEntity;
import twitter4j.Status;


@Component
public class TweetToAdHandler {

    private static final String API_URI = "https://api.marktplaats.dev/v1";
    private static final String ACCESS_TOKEN = "zVYqrAUaNjjIc1YhgiEGDZSv3";
    private static final String MP_BASE_URL = "aurora.marktplaats.dev";

    private static final Logger logger = LoggerFactory.getLogger(TweetToAdHandler.class);
    private HttpClient httpClient = HttpClientFactory.newHttpClient();

    @Autowired
    private LinkDao linkDao;

    @Autowired
    private AuthService.Iface authService;

    public boolean isPostedByLinkedUser(String rawTweet) {
        long tweetId = getIdFromTweetJson(rawTweet);
        Status tweet = new TweetRetriever().getTweet(tweetId);
        String screenname = tweet.getUser().getScreenName();
        return linkDao.findByTwitterScreenName(screenname) != null;
    }

    public void doMagic(String rawTweet) {
        // first get the full tweet
        long tweetId = getIdFromTweetJson(rawTweet);
        Status tweet = new TweetRetriever().getTweet(tweetId);
        List<String> images = getImagesUrlFromTweetJson(tweet);
        String item = new TweetToItemConverter().convertTweetToItem(tweet.getText());
        TwitterUser twitteruser = new TwitterUser(tweet.getUser().getScreenName(), tweet.getUser().getId());

        Link link = linkDao.findByTwitterScreenName(twitteruser.screenname);
        long marktplaatsUserId = link.getMarktplaatsUserId();

        System.out.println(item);
        String postedAd = postAd(marktplaatsUserId, item);
        uploadImages(postedAd, images);
        tweetUpdate(postedAd, twitteruser, tweetId);
        System.out.println("posted ad " + postedAd);
    }

    private List<String> getImagesUrlFromTweetJson(Status rawTweet) {
        final List<MediaEntity> mediaEntities = Arrays.asList(rawTweet.getMediaEntities());

        return Lists.transform(mediaEntities, new Function<MediaEntity, String>() {
            @Override
            public String apply(MediaEntity mediaEntity) {
                return mediaEntity.getMediaURL();
            }
        });
    }

    private long getIdFromTweetJson(String rawTweet) {
        JsonParser parser = new JsonParser();
        JsonObject obj = parser.parse(rawTweet).getAsJsonObject();
        return obj.get("id").getAsLong();
    }

    private void tweetUpdate(String adJson, TwitterUser twitteruser, long originalTweetId) {
        String url = String.format("http://%s/%s", MP_BASE_URL, getItemId(adJson));
        String tweetText = String.format("Nieuwe advertentie van @%s: %s %s", twitteruser.screenname, getTitle(adJson), url);
        new TwitterUpdate().sendUpdate(tweetText, originalTweetId);
    }

    private String getItemId(String adJson) {
        JsonParser parser = new JsonParser();
        JsonObject obj = parser.parse(adJson).getAsJsonObject();
        return obj.get("itemId").toString().replace("\"", "");
    }

    private String getTitle(String adJson) {
        JsonParser parser = new JsonParser();
        JsonObject obj = parser.parse(adJson).getAsJsonObject();
        return obj.get("title").toString().replace("\"", "");
    }

    private void uploadImages(String postedAd, List<String> images) {
        if (images.isEmpty()) {
            return;
        }

        String itemId = getItemId(postedAd);

        List<String> imagesWithQuote = Lists.transform(images, new Function<String, String>() {
            @Override
            public String apply(String s) {
                return "\"" + s + "\"";
            }
        });
        String imagesJson = "{\"urls\":[" + Joiner.on(",").join(imagesWithQuote) + "],\"replaceAll\":true}";
        System.out.println(imagesJson);
        try {
            HttpPost httpPost = new HttpPost(API_URI + String.format("/advertisements/%s/images", itemId));
            httpPost.addHeader(new BasicHeader("Authorization", "Bearer " + ACCESS_TOKEN));
            httpPost.setEntity(new StringEntity(imagesJson, ContentType.APPLICATION_JSON));
            HttpResponse httpResponse = httpClient.execute(httpPost);
            if (httpResponse.getStatusLine().getStatusCode() == 202) {
                logger.info("Images are uploaded: status: %d", httpResponse.getStatusLine().getStatusCode());
            } else {
                logger.error(String.format("Wrong!: status: %d, body: %s", httpResponse.getStatusLine().getStatusCode(), EntityUtils.toString(httpResponse.getEntity())));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String postAd(long userId, String item) {
        try {

            String token = createTokenForUser(userId);

            HttpPost httpPost = new HttpPost(API_URI + "/advertisements");
            httpPost.addHeader(new BasicHeader("Authorization", "Bearer " + token));
            httpPost.setEntity(new StringEntity(item, ContentType.APPLICATION_JSON));
            HttpResponse httpResponse = httpClient.execute(httpPost);
            if (httpResponse.getStatusLine().getStatusCode() == 201) {
                return EntityUtils.toString(httpResponse.getEntity());
            } else {
                logger.error(String.format("Wrong!: status: %d, body: %s", httpResponse.getStatusLine().getStatusCode(), EntityUtils.toString(httpResponse.getEntity())));
                return "";
            }
        } catch (IOException | TException e) {
            e.printStackTrace();
        }
        return "";
    }


    private String createTokenForUser(long userId) throws IOException, TException {
        String bearerToken = UUID.randomUUID().toString();

        MarktplaatsAccessToken token = new MarktplaatsAccessToken();
        token.setAccessToken(bearerToken);
        token.setClientId("tweetmarkt");
        token.setExpireDate(Long.MAX_VALUE);
        token.setRefreshToken("");
        token.setUserId((int) userId);

        authService.storeMarktplaatsAccessToken(new RequestData(), token);
        return bearerToken;
    }


    class TwitterUser {
        public String screenname;
        public long id;

        TwitterUser(String screenname, long id) {
            this.screenname = screenname;
            this.id = id;
        }
    }
}
