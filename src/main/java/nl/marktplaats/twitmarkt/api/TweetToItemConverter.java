package nl.marktplaats.twitmarkt.api;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.Gson;

public class TweetToItemConverter {
    public String convertTweetToItem(String rawTweet) {
        String tweet = rawTweet.replace("\"","").replaceAll("#.*", "").trim();
        Pattern pattern = Pattern.compile("(.*): (.*) - (.*)");
        Matcher matcher = pattern.matcher(tweet);
        if (matcher.find()) {
            String category = matcher.group(1);
            String title = matcher.group(2);
            String description = matcher.group(3);
            return objectToJson(new Item(title, description, new PriceModel(), categoryToId(category), "gebruikt"));
        }
        return null;
    }

    private String objectToJson(Item item) {
        return new Gson().toJson(item);
    }

    private String getConditieAttribute(int categoryId) {
        if (categoryId == 1953) {
            return "gebruikt";
        } else {
            return null;
        }
    }


    private int categoryToId(String category) {
        if (category.toLowerCase().equals("iphone") || category.toLowerCase().equals("apple iphone")) {
            return 1953;
        } else {
            return 2;
        }
    }
}
