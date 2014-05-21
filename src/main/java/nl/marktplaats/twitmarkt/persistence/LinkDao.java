package nl.marktplaats.twitmarkt.persistence;

import nl.marktplaats.twitmarkt.model.Link;
import org.springframework.dao.DataAccessException;

public interface LinkDao {

    Link findByMarktplaatsUserId(long userId) throws DataAccessException;

    Link findByTwitterScreenName(String screenName) throws DataAccessException;

    void save(Link link) throws DataAccessException;

}
