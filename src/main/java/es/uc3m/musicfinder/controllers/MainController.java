package es.uc3m.musicfinder.controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import es.uc3m.musicfinder.model.Post;
import es.uc3m.musicfinder.model.User;

@Controller
@RequestMapping(path = "/")
public class MainController {

    @GetMapping(path = "/")
    public String mainView(Model model) {

        List<Post> Posts = new ArrayList<Post>();

        User user = new User();
        user.setId(1);
        user.setEmail("mary@example.com");
        user.setName("mary");

        Post Post = new Post();
        Post.setId(1);
        Post.setUser(user);
        Post.setText("Test post");
        Post.setTimestamp(new Date());
        Posts.add(Post);

        Post = new Post();
        Post.setId(2);
        Post.setUser(user);
        Post.setText("Another post");
        Post.setTimestamp(new Date());
        Posts.add(Post);

        model.addAttribute("Posts", Posts);
        return "main_view";
    }
}
