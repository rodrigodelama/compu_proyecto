package es.uc3m.microblog.controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import es.uc3m.microblog.model.Message;
import es.uc3m.microblog.model.User;

@Controller
@RequestMapping(path = "/")
public class MainController {

    @GetMapping(path = "/")
    public String mainView(Model model) {
        List<Message> messages = new ArrayList<Message>();
        User user = new User();
        user.setId(1);
        user.setEmail("mary@example.com");
        user.setName("mary");
        Message message = new Message();
        message.setId(1);
        message.setUser(user);
        message.setText("Test post");
        message.setTimestamp(new Date());
        messages.add(message);
        message = new Message();
        message.setId(2);
        message.setUser(user);
        message.setText("Another post");
        message.setTimestamp(new Date());
        messages.add(message);
        model.addAttribute("messages", messages);
        return "main_view";
    }
}
