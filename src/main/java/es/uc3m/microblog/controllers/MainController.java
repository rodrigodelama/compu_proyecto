package es.uc3m.microblog.controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import es.uc3m.microblog.model.Listing;
import es.uc3m.microblog.model.User;

@Controller
@RequestMapping(path = "/")
public class MainController {

    @GetMapping(path = "/")
    public String mainView(Model model) {

        List<Listing> listings = new ArrayList<Listing>();

        User user = new User();
        user.setId(1);
        user.setEmail("mary@example.com");
        user.setName("mary");

        Listing listing = new Listing();
        listing.setId(1);
        listing.setUser(user);
        listing.setText("Test post");
        listing.setTimestamp(new Date());
        listings.add(listing);

        listing = new Listing();
        listing.setId(2);
        listing.setUser(user);
        listing.setText("Another post");
        listing.setTimestamp(new Date());
        listings.add(listing);

        model.addAttribute("listings", listings);
        return "main_view";
    }
}
