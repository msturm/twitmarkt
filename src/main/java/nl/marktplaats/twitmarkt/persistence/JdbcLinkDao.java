package nl.marktplaats.twitmarkt.persistence;

import nl.marktplaats.twitmarkt.model.Link;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
public class JdbcLinkDao implements LinkDao {

    private final LinkRowMapper rowMapper = new LinkRowMapper();
    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public JdbcLinkDao(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Link findByMarktplaatsUserId(final long userId) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("marktplaats_user_id", userId);

        List<Link> results = jdbcTemplate.query("select id, marktplaats_user_id, twitter_screen_name from link where marktplaats_user_id = :marktplaats_user_id",
                parameters, rowMapper);

        return DataAccessUtils.singleResult(results);
    }

    @Override
    public Link findByTwitterScreenName(final String screenName) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("twitter_screen_name", screenName);

        List<Link> results = jdbcTemplate.query("select id, marktplaats_user_id, twitter_screen_name from link where twitter_screen_name = :twitter_screen_name",
                parameters, rowMapper);

        return DataAccessUtils.singleResult(results);
    }

    @Override
    public void save(Link link) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("marktplaats_user_id", link.getMarktplaatsUserId());
        parameters.put("twitter_screen_name", link.getTwitterScreenName());

        jdbcTemplate.update("insert into link (marktplaats_user_id, twitter_screen_name) values (:marktplaats_user_id, :twitter_screen_name)", parameters);
    }


    private static class LinkRowMapper implements RowMapper<Link> {
        @Override
        public Link mapRow(ResultSet rs, int rowNum) throws SQLException {
            long id = rs.getLong("id");
            long marktplaatsUserId = rs.getLong("marktplaats_user_id");
            String twitterScreenName = rs.getString("twitter_screen_name");
            return new Link(id, twitterScreenName, marktplaatsUserId);
        }

    }
}
