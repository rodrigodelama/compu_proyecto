package es.uc3m.musicfinder.controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import es.uc3m.musicfinder.model.Message;
import es.uc3m.musicfinder.model.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import es.uc3m.musicfinder.model.UserRepository;
import es.uc3m.musicfinder.services.UserService;

import org.springframework.validation.BindingResult;
import jakarta.validation.Valid;

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

        message = new Message();
        message.setId(3);
        message.setUser(user);
        message.setText("A new post");
        message.setTimestamp(new Date());
        messages.add(message);
        
        model.addAttribute("messages", messages);
        return "main_view";
    }

    @GetMapping(path = "/user")
    public String userView(Model model) {

        List<Message> messages = new ArrayList<Message>();

        User user2 = new User();
        user2.setId(2);
        user2.setEmail("james@exercise2.com");
        user2.setName("james");

        Message message = new Message();
        message.setId(1);
        message.setUser(user2);
        message.setText("Test post");
        message.setTimestamp(new Date());
        messages.add(message);

        message = new Message();
        message.setId(2);
        message.setUser(user2);
        message.setText("Another post");
        message.setTimestamp(new Date());
        messages.add(message);

        model.addAttribute("user", user2);
        model.addAttribute("messages", messages);
        return "user_view";
    }

    @GetMapping(path = "/message")
    public String messageView(Model model) {
        
        List<Message> messages = new ArrayList<Message>();

        User user3 = new User();
        user3.setId(3);
        user3.setEmail("max@exercise5.com");
        user3.setName("max");

        Message message = new Message();
        message.setId(2);
        message.setUser(user3);
        message.setText("Another post");
        message.setTimestamp(new Date());
        messages.add(message);

        model.addAttribute("user", user3);
        model.addAttribute("messages", messages);
        return "message_view";
    }

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;


    // SIGNUP
    // signup form
    @GetMapping(path = "/signup")
    public String signUpForm() {
        return "signup";
    }

    // signup submission
    @PostMapping(path = "/signup")
    public String signUp(@Valid @ModelAttribute("user") User user,
                        BindingResult bindingResult,
                        @RequestParam(name = "passwordRepeat") String passwordRepeat) {
        if (bindingResult.hasErrors()) {
            return "signup";
        }
        if (userRepository.findByEmail(user.getEmail()) != null) {
            return "redirect:signup?duplicate_email";
        }
        if (!user.getPassword().equals(passwordRepeat)) {
            return "redirect:signup?passwords";
        }
        userService.register(user);
        return "redirect:login?registered";
    }
    //


    // LOGIN
    @GetMapping(path = "/login")
    public String loginForm() {
        return "login";
    }
    //
}
