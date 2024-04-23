package es.uc3m.musicfinder.controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import es.uc3m.musicfinder.model.Event;
import es.uc3m.musicfinder.model.User;

@Controller
@RequestMapping(path = "/")
public class MainController {

    @GetMapping(path = "/")
    public String mainView(Model model) {

        List<Event> Events = new ArrayList<Event>();

        User user = new User();
        user.setId(1);
        user.setEmail("mary@example.com");
        user.setName("mary");

        Event Event = new Event();
        Event.setId(1);
        Event.setUser(user);
        Event.setText("Test Event");
        Event.setTimestamp(new Date());
        Events.add(Event);

        Event = new Event();
        Event.setId(2);
        Event.setUser(user);
        Event.setText("Another Event");
        Event.setTimestamp(new Date());
        Events.add(Event);

        model.addAttribute("Events", Events);
        return "main_view";
    }
}
