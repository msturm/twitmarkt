package nl.marktplaats.twitmarkt;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import nl.marktplaats.twitmarkt.api.TweetToItemConverter;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: msturm
 * Date: 21-05-14
 * Time: 14:18
 */
public class TweetToAdHandler {

    private static final String API_URI = "https://api.marktplaats.dev/v1";
    private static final String ACCESS_TOKEN = "zVYqrAUaNjjIc1YhgiEGDZSv3";

    private static final Logger logger = LoggerFactory.getLogger(TweetToAdHandler.class);
    private HttpClient httpClient = HttpClientFactory.newHttpClient();

    public void doMagic(String rawTweet) {
        JsonParser parser = new JsonParser();
        JsonObject obj = parser.parse(rawTweet).getAsJsonObject();
        String tweet = obj.get("text").toString();
        String item = new TweetToItemConverter().convertTweetToItem(tweet);
        System.out.println(item);
        String postedAd = postAd(item);
        System.out.println("posted ad " + postedAd);
    }

    private String postAd(String item) {
        try {
            HttpPost httpPost = new HttpPost(API_URI + "/advertisements");
            httpPost.addHeader(new BasicHeader("Authorization", "Bearer " + ACCESS_TOKEN));
            httpPost.setEntity(new StringEntity(item, ContentType.APPLICATION_JSON));
            HttpResponse httpResponse = httpClient.execute(httpPost);
            if (httpResponse.getStatusLine().getStatusCode() == 201) {
                return EntityUtils.toString(httpResponse.getEntity());
            } else {
                logger.error(String.format("Wrong!: status: %d, body: %s", httpResponse.getStatusLine().getStatusCode(), EntityUtils.toString(httpResponse.getEntity())));
                return "";
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
