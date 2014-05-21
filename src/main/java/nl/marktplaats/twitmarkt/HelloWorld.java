package nl.marktplaats.twitmarkt;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * User: msturm
 * Date: 21-05-14
 * Time: 10:23
 */
@Controller
public class HelloWorld {

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String hello() {
        return "hoi";
    }

}
