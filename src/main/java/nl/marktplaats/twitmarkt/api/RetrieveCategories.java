package nl.marktplaats.twitmarkt.api;

import java.io.IOException;
import java.util.Map;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import nl.marktplaats.twitmarkt.HttpClientFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: msturm
 * Date: 22-05-14
 * Time: 11:31
 */
public class RetrieveCategories {

    private static final String API_URI = "https://api.marktplaats.dev/v1";
    private HttpClient httpClient = HttpClientFactory.newHttpClient();
    private static final Logger logger = LoggerFactory.getLogger(RetrieveCategories.class);

    public Map<String, CategoryInfo> retrieveL2Categories(String token) {
        Map<String, CategoryInfo> l2Categories = Maps.newHashMap();
        try {
            HttpGet httpGet = new HttpGet(API_URI + String.format("/categories"));
            httpGet.addHeader(new BasicHeader("Authorization", "Bearer " + token));
            HttpResponse httpResponse = httpClient.execute(httpGet);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                logger.info("Images are uploaded: status: %d", httpResponse.getStatusLine().getStatusCode());
            } else {
                logger.error(String.format("Wrong!: status: %d, body: %s", httpResponse.getStatusLine().getStatusCode(), EntityUtils.toString(httpResponse.getEntity())));
            }

            String categoriesJson = EntityUtils.toString(httpResponse.getEntity());

            JsonParser parser = new JsonParser();
            JsonObject obj = parser.parse(categoriesJson).getAsJsonObject();
            for (JsonElement embedded : obj.getAsJsonObject("_embedded").getAsJsonArray("mp:category")) {
                long l1CatId = embedded.getAsJsonObject().get("categoryId").getAsLong();
                for (JsonElement l2Element : embedded.getAsJsonObject().getAsJsonObject("_embedded").getAsJsonArray("mp:category")) {
                    String name = l2Element.getAsJsonObject().get("name").getAsString();
                    Long l2CatId = l2Element.getAsJsonObject().get("categoryId").getAsLong();
                    l2Categories.put(name, new CategoryInfo(l2CatId, hasMandatoryCondition(l1CatId, l2CatId, token)));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return l2Categories;
    }

    private boolean hasMandatoryCondition(long l1CatId, long l2CatId, String token) {
        try {
            HttpGet httpGet = new HttpGet(API_URI + String.format("/categories/%d/%d/attributes", l1CatId, l2CatId));
            httpGet.addHeader(new BasicHeader("Authorization", "Bearer " + token));
            HttpResponse httpResponse = httpClient.execute(httpGet);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                String attrJson = EntityUtils.toString(httpResponse.getEntity());
                JsonParser parser = new JsonParser();
                JsonObject obj = parser.parse(attrJson).getAsJsonObject();
                for (JsonElement jsonElement : obj.getAsJsonArray("fields")) {
                    JsonObject attribute = jsonElement.getAsJsonObject();
                    if (attribute.get("key").getAsString().equals("conditie")) {
                        return attribute.get("mandatory").getAsBoolean();
                    }
                }
            } else {
                logger.error(String.format("Wrong!: status: %d, body: %s", httpResponse.getStatusLine().getStatusCode(), EntityUtils.toString(httpResponse.getEntity())));
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
