package nl.marktplaats.twitmarkt.api;

/**
 * User: msturm
 * Date: 21-05-14
 * Time: 14:45
 */
public class Location {
    private String postcode = "1097DN";

    public Location() {
    }

    public Location(String postcode) {
        this.postcode = postcode;
    }

    public String getPostcode() {
        return postcode;
    }
}
