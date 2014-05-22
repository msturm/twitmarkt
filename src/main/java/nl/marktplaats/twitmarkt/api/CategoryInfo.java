package nl.marktplaats.twitmarkt.api;

/**
 * User: msturm
 * Date: 22-05-14
 * Time: 14:06
 */
public class CategoryInfo {
    public long id;
    public boolean mandatoryCondition;

    CategoryInfo(long id, boolean mandatoryCondition) {
        this.id = id;
        this.mandatoryCondition = mandatoryCondition;
    }


    @Override
    public String toString() {
        return "CategoryInfo{" +
                "id=" + id +
                ", mandatoryCondition=" + mandatoryCondition +
                '}';
    }
}
