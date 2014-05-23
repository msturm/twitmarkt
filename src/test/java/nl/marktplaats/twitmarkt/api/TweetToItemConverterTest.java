package nl.marktplaats.twitmarkt.api;

import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;

import com.google.common.collect.Maps;
import org.junit.Test;

public class TweetToItemConverterTest {

    @Test
    public void tweetShouldBeConvertedToItem() {
        String tweet = "iPhone: iPhone 4 met hoesje - Gebruikte iPhone 4 met hoesje en oplader. #marktplaatsad";
        String result = new TweetToItemConverter().convertTweetToItem(tweet, Maps.<String, CategoryInfo>newHashMap());
        assertThat(result, startsWith("{\"title\":\"iPhone 4 met hoesje\"," +
                "\"description\":\"Gebruikte iPhone 4 met hoesje en oplader.\""));
//        assertThat(result, containsString("\"categoryId\":1953"));

    }

    @Test
    public void carTweetShouldBeConvertedToItem() {
//        String tweet = "Volkswagen Golf 3 'Match'. Bouwjaar 1996. Iemand geïnteresseerd? Indien niet: RT ;-)";
        String tweet = "#tekoop Volkswagen Golf 3 'Match'. Bouwjaar 1996. Iemand geïnteresseerd? #marktplaats http:\\/\\/t.co\\/3PTHlB5GrT";
        String result = new TweetToItemConverter().convertTweetToItem(tweet, Maps.<String, CategoryInfo>newHashMap());
        assertThat(result, startsWith("{\"title\":\"Volkswagen Golf 3 \\u0027Match\\u0027.\"," +
                "\"description\":\"Bouwjaar 1996. Iemand geïnteresseerd? Indien niet: RT ;-)\""));
//        assertThat(result, containsString("\"categoryId\":1953"));

    }
}