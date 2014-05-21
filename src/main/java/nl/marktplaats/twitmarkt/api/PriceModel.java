package nl.marktplaats.twitmarkt.api;

/**
 * User: msturm
 * Date: 21-05-14
 * Time: 13:08
 */
public class PriceModel {
    private String modelType = "bidding";
    private int minimalBid = 0;

    public PriceModel() {
    }

    public PriceModel(String modelType, int minimalBid) {
        this.modelType = modelType;
        this.minimalBid = minimalBid;
    }

    public String getModelType() {
        return modelType;
    }

    public int getMinimalBid() {
        return minimalBid;
    }
}
