package nl.marktplaats.twitmarkt.api;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

public class TweetToItemConverterTest {

    @Test
    public void tweetShouldBeConvertedToItem() {
        String tweet = "iPhone: iPhone 4 met hoesje - Gebruikte iPhone 4 met hoesje en oplader. #marktplaatsad";
        String result = new TweetToItemConverter().convertTweetToItem(tweet);
        assertThat(result, startsWith("{\"title\":\"iPhone 4 met hoesje\"," +
                "\"description\":\"Gebruikte iPhone 4 met hoesje en oplader.\""));
        assertThat(result, containsString("\"categoryId\":1953"));

    }
}