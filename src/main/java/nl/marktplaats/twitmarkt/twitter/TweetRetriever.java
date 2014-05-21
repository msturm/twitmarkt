package nl.marktplaats.twitmarkt.twitter;

import static nl.marktplaats.twitmarkt.twitter.TwitterAuthentication.consumerKey;
import static nl.marktplaats.twitmarkt.twitter.TwitterAuthentication.consumerSecret;
import static nl.marktplaats.twitmarkt.twitter.TwitterAuthentication.token;
import static nl.marktplaats.twitmarkt.twitter.TwitterAuthentication.tokenSecret;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

/**
 * User: msturm
 * Date: 21-05-14
 * Time: 17:07
 */
public class TweetRetriever {

    public twitter4j.Status getTweet(long tweetId) {
        Twitter twitter = new TwitterFactory().getInstance();
        twitter.setOAuthConsumer(consumerKey, consumerSecret);
        twitter.setOAuthAccessToken(new AccessToken(token, tokenSecret));

        try {
            return twitter.showStatus(tweetId);
        } catch (TwitterException e) {
            e.printStackTrace();
        }
        return null;
    }
}
