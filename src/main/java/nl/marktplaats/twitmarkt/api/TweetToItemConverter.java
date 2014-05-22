package nl.marktplaats.twitmarkt.api;


import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.Gson;

public class TweetToItemConverter {
    public String convertTweetToItem(String rawTweet, Map<String, CategoryInfo> l2Categories) {
        String tweet = rawTweet.replace("\"","").replaceAll("#.*", "").trim();
        Pattern pattern = Pattern.compile("(.*): (.*) - (.*)");
        Matcher matcher = pattern.matcher(tweet);
        if (matcher.find()) {
            String category = matcher.group(1);
            String title = matcher.group(2);
            String description = matcher.group(3);
            CategoryInfo categoryId = categoryToId(category, l2Categories);
            return objectToJson(new Item(title, description, new PriceModel(), categoryId.id, getConditieAttribute(categoryId)));
        }
        return null;
    }

    private String objectToJson(Item item) {
        return new Gson().toJson(item);
    }

    private String getConditieAttribute(CategoryInfo category) {
        if (category.mandatoryCondition) {
            return "gebruikt";
        } else {
            return null;
        }
    }


    private CategoryInfo categoryToId(final String category, Map<String, CategoryInfo> l2Categories) {
        List<String> matchingCats = Lists.newArrayList(Sets.filter(l2Categories.keySet(), new Predicate<String>() {
            public boolean apply(String s) {
                return s.toLowerCase().contains(category.toLowerCase());
            }
        }));

        CategoryInfo matchingCatId = new CategoryInfo(2, false);
        if (! matchingCats.isEmpty()) {
            matchingCatId = l2Categories.get(matchingCats.get(0));
        }
        System.out.println("Matching category: " + matchingCatId);
        return matchingCatId;
    }
}
