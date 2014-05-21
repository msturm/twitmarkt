package nl.marktplaats.twitmarkt.model;

import java.io.Serializable;

public class Link implements Serializable {

    private long id;
    private String twitterScreenName;
    private long marktplaatsUserId;

    public Link(String twitterScreenName, long marktplaatsUserId) {
        this(-1, twitterScreenName, marktplaatsUserId);
    }

    public Link(long id, String twitterScreenName, long marktplaatsUserId) {
        this.id = id;
        this.twitterScreenName = twitterScreenName;
        this.marktplaatsUserId = marktplaatsUserId;
    }

    public long getId() {
        return id;
    }

    public String getTwitterScreenName() {
        return twitterScreenName;
    }

    public long getMarktplaatsUserId() {
        return marktplaatsUserId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Link link = (Link) o;

        if (id != link.id) return false;
        if (marktplaatsUserId != link.marktplaatsUserId) return false;
        if (!twitterScreenName.equals(link.twitterScreenName)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + twitterScreenName.hashCode();
        result = 31 * result + (int) (marktplaatsUserId ^ (marktplaatsUserId >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "Link{" +
                "id=" + id +
                ", twitterScreenName='" + twitterScreenName + '\'' +
                ", marktplaatsUserId=" + marktplaatsUserId +
                '}';
    }
}
