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
        String tweet = rawTweet.replace("\"","").replaceAll("#\\w*?", "").trim();
        Pattern pattern1 = Pattern.compile("(.*): (.*) - (.*)");
        Matcher matcher1 = pattern1.matcher(tweet);
        if (matcher1.find()) {
            String category = matcher1.group(1);
            String title = matcher1.group(2);
            String description = matcher1.group(3);
            CategoryInfo categoryId = categoryToId(category, l2Categories);
            return objectToJson(new Item(title, description, new PriceModel(), categoryId.id, getConditieAttribute(categoryId)));
        }

        Pattern pattern2 = Pattern.compile("((\\w+).*?\\.)(.*)");
        Matcher matcher2 = pattern2.matcher(tweet);
        if (matcher2.find()) {
            String category = matcher2.group(2);
            String title = matcher2.group(1).trim();
            String description = matcher2.group(3).trim();
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
