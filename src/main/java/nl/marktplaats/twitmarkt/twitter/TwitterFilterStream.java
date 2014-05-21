package nl.marktplaats.twitmarkt.twitter;


import static nl.marktplaats.twitmarkt.twitter.TwitterAuthentication.consumerKey;
import static nl.marktplaats.twitmarkt.twitter.TwitterAuthentication.consumerSecret;
import static nl.marktplaats.twitmarkt.twitter.TwitterAuthentication.token;
import static nl.marktplaats.twitmarkt.twitter.TwitterAuthentication.tokenSecret;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import javax.annotation.PostConstruct;

import com.google.common.collect.Lists;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;
import nl.marktplaats.twitmarkt.TweetToAdHandler;
import org.springframework.stereotype.Component;


/**
 * User: msturm
 * Date: 21-05-14
 * Time: 13:35
 */
@Component
public class TwitterFilterStream {

    TweetToAdHandler handler = new TweetToAdHandler();

    public void oauth(String consumerKey, String consumerSecret, String token, String secret) throws InterruptedException {
        BlockingQueue<String> queue = new LinkedBlockingQueue<String>(10000);
        StatusesFilterEndpoint endpoint = new StatusesFilterEndpoint();
        // add some track terms
        endpoint.trackTerms(Lists.newArrayList("#marktplaatsad"));

        Authentication auth = new OAuth1(consumerKey, consumerSecret, token, secret);
        // Authentication auth = new BasicAuth(username, password);

        // Create a new BasicClient. By default gzip is enabled.
        Client client = new ClientBuilder()
                .hosts(Constants.STREAM_HOST)
                .endpoint(endpoint)
                .authentication(auth)
                .processor(new StringDelimitedProcessor(queue))
                .build();

        // Establish a connection
        client.connect();

        // Do whatever needs to be done with messages
        for (int msgRead = 0; msgRead < 1000; msgRead++) {
            String msg = queue.take();
            System.out.println(msg);

            if (handler.isPostedByLinkedUser(msg)) {
                handler.doMagic(msg);
            } else {
                System.out.println("This is not a user in the link table!!!");
            }
        }

        client.stop();

    }

    @PostConstruct
    public void afterPropertiesSet() throws Exception {
        System.out.println("Starting stream listener");
        try {
            new TwitterFilterStream().oauth(consumerKey, consumerSecret, token, tokenSecret);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
