package nl.marktplaats.twitmarkt.web;

import nl.marktplaats.twitmarkt.model.Link;
import nl.marktplaats.twitmarkt.persistence.LinkDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@Controller
@RequestMapping("/links")
public class LinkController {

    private final LinkDao  linkDao;

    @Autowired
    public LinkController(LinkDao linkDao) {
        this.linkDao = linkDao;
    }

    @RequestMapping(value = "/{userId}", method = PUT)
    public ResponseEntity save(@PathVariable long userId, @RequestBody String twitterName) {
        Link link = linkDao.findByMarktplaatsUserId(userId);

        if (link != null) {
            linkDao.delete(link);
        }

        linkDao.save(new Link(twitterName ,userId));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{userId}", method = DELETE)
    public ResponseEntity delete(@PathVariable long userId) {
        Link link = linkDao.findByMarktplaatsUserId(userId);
        if (link != null) {
            linkDao.delete(link);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/{userId}", method = GET)
    public ResponseEntity<String> get(@PathVariable long userId) {
        Link link = linkDao.findByMarktplaatsUserId(userId);

        if (link == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        return new ResponseEntity<>(link.getTwitterScreenName(), headers, HttpStatus.OK);
    }

}
