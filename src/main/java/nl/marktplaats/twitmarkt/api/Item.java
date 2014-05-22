package nl.marktplaats.twitmarkt.api;

/**
 * User: msturm
 * Date: 21-05-14
 * Time: 13:05
 */
public class Item {
    private String title;
    private String description;
    private PriceModel priceModel;
    private long categoryId;
    private String conditie;
    private Location location = new Location();

    public Item(String title, String description, PriceModel priceModel, long categoryId, String conditie, Location location) {
        this.title = title;
        this.description = description;
        this.priceModel = priceModel;
        this.categoryId = categoryId;
        this.conditie = conditie;
        this.location = location;
    }

    public Item(String title, String description, PriceModel priceModel, long categoryId, String conditie) {
        this.title = title;
        this.description = description;
        this.priceModel = priceModel;
        this.categoryId = categoryId;
        this.conditie = conditie;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public PriceModel getPriceModel() {
        return priceModel;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public String getConditie() {
        return conditie;
    }
}

