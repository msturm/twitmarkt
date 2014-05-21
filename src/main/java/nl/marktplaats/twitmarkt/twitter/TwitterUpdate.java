package nl.marktplaats.twitmarkt.twitter;

import static nl.marktplaats.twitmarkt.twitter.TwitterAuthentication.consumerKey;
import static nl.marktplaats.twitmarkt.twitter.TwitterAuthentication.consumerSecret;
import static nl.marktplaats.twitmarkt.twitter.TwitterAuthentication.token;
import static nl.marktplaats.twitmarkt.twitter.TwitterAuthentication.tokenSecret;

import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

/**
* User: msturm
* Date: 21-05-14
* Time: 15:21
*/
public class TwitterUpdate {

    public void sendUpdate(String tweet, long replyToStatusId) {
        Twitter twitter = new TwitterFactory().getInstance();
        twitter.setOAuthConsumer(consumerKey, consumerSecret);
        twitter.setOAuthAccessToken(new AccessToken(token, tokenSecret));
        try {
            StatusUpdate statusUpdate = new StatusUpdate(tweet);
            statusUpdate.inReplyToStatusId(replyToStatusId);
            twitter.updateStatus(statusUpdate);
        } catch (TwitterException e) {
            e.printStackTrace();
        }
    }
}
