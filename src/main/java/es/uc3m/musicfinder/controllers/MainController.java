package es.uc3m.musicfinder.controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping; // p6

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam; // p6

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ModelAttribute; // p6

import org.springframework.web.server.ResponseStatusException;

import org.springframework.http.HttpStatus;

import org.springframework.data.domain.PageRequest;

import org.springframework.beans.factory.annotation.Autowired; // p6

// p6 signup
import org.springframework.validation.BindingResult;
import jakarta.validation.Valid;

// Model & Services
import es.uc3m.musicfinder.model.*;
import es.uc3m.musicfinder.services.*;

@Controller
@RequestMapping(path = "/")
public class MainController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private RecommendationRepository recommendationRepository;

    @GetMapping(path = "/")
    public String mainView(Model model, Principal principal) {
        // Check login status
        if (principal != null) {
            // If logged in, retrieve the current user and load recommendations
            String userEmail = principal.getName(); // Get the email from Principal
            User currentUser = userRepository.findByEmail(userEmail);
            String role = currentUser.getRole();
            
            if (currentUser != null) {
                List<Recommendation> recommendations = recommendationRepository.findByRecommendTo(currentUser);
                model.addAttribute("recommendations", recommendations); // Add recommendations to model
            }
        }

        // Regardless, always load all events
        List<Event> events = eventRepository.findAll();
        model.addAttribute("events", events);

        return "home"; // Return the home view
    }

    @GetMapping(path = "/error")
    public String errorView() {
        return "error";
    }

    @GetMapping(path = "/signup")
    public String signUpForm(User user) {
        return "signup";
    }
    @PostMapping(path = "/signup")
    public String signUp(@Valid @ModelAttribute("user") User user, BindingResult result,
                        @RequestParam(name = "passwordRepeat") String passwordRepeat) {
        if (result.hasErrors()) {
            return "signup";
        }
        if (userRepository.findByEmail(user.getEmail()) != null) {
            return "redirect:signup?email_already_registered";
        }
        if (!user.getPassword().equals(passwordRepeat)) {
            return "redirect:signup?password_mismatch";
        }
        userService.register(user);
        return "redirect:login?registered_succesfully";
    }

    @GetMapping(path = "/login")
    public String loginForm() {
        return "login";
    }

    @GetMapping(path = "/event")
    public String eventView(Model model) {
        return "event";
    }

    @GetMapping(path = "/event/{eventID}")
    public String eventView(@PathVariable int userId, Model model, Principal principal) {

        return "event";
    }

    @GetMapping(path = "/user")
    public String userView(Model model) {
        // List<Message> messages = new ArrayList<Message>();
        // User user = new User();
        // Message message = new Message();

        // model.addAttribute("messages", messages);
        return "user";
    }
    @GetMapping(path = "/user/{username}")
    public String userView(@PathVariable int userId, Model model, Principal principal) {
        String followButton = "";
        User currentUser = userRepository.findByEmail(principal.getName());
        Optional<User> userOpt = userRepository.findById(userId);

        // para que nadie tenga info sobre el server que corre la app.
        if (!userOpt.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Message not found");
        }
        //Ej 3 p8: ocultación de mensajes de respuesta en vista de perfil de usuario.
        // List<Message> messages = messageRepository.findByUserAndResponseToIsNullOrderByTimestampDesc(userOpt.get());
        // model.addAttribute("user", userOpt.get());
        // model.addAttribute("messages", messages);

        // Ej 7 p8 (valor de la cadena "followButton")----------------------
        if(currentUser.equals(userOpt.get())) {
            followButton = "none";
        }
        if(userService.follows(currentUser, userOpt.get())) {
            followButton = "unfollow"; // --------------------------------------- this is useed for favoriting events
        } else {
            followButton = "follow";
        }
        model.addAttribute("followButton", followButton);
        /*
         * "none" (cuando el usuario actual y el usuario mostrado son el mismo)
         * "follow" (cuando el usuario actual todavía no está siguiendo al usuario mostrado en esa vista de perfil)
         * "unfollow" (cuando el usuario actual ya está siguiendo al usuario mostrado en esa vista de perfil).
         */
        return "user";
    }
    


    /*
    // Ej 3 P7: New code for the controller method of the message view:
    @GetMapping(path = "/message/{messageId}")
    public String messageView(@PathVariable int messageId, Model model) {
        Optional<Message> messageOpt = messageRepository.findById(messageId);
        if (!messageOpt.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Message not found");
        }
        //Ej 3 p8: ocultación de mensajes de respuesta en vista de mensaje.
        if(messageOpt.get().getResponseTo() != null){
            //Si el mensaje es una respuesta, no lo mostramos y devolvemos prohibido.
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Message is a response");
        }
        List<Message> messages = messageRepository.findByResponseToOrderByTimestampAsc(messageOpt.get());
        //! Duda: porqué no hacemos lo de optional con esto también?
        // if(messages !=null){//Si no hay respuestas, no las añadimos (por si acaso).
        //     model.addAttribute("respuestas", messages);
        // }
        model.addAttribute("respuestas", messages);//Aunque no haya respuestas, se añade la lista vacía.
            //Posible mejora: no poder responder a mensajes que son tuyos (sería teniendo "principal" en el método y viendo si el mensaje que 
            //se está viendo es del usuario loggeado).
            /*
             * currentUser = userRepository.findByEmail(principal.getName());
             * if(currentUser.equals(messageOpt.get().getUser())){
             * is_owner = 'mine';
             * Y luego haríamos como el if en el html para que no se muestre el botón de responder ni tampoco el formulario de respuesta.
             *
        model.addAttribute("message", messageOpt.get());
        return "message_view";
    }
    
    //Nuevo metodo para ruta /post
    @PostMapping(path = "/post")
    public String postMessage(@ModelAttribute Message message, Principal principal) {
        User user = userRepository.findByEmail(principal.getName());
        message.setUser(user);
        message.setTimestamp(new Date());
        messageRepository.save(message);
        //Lo de las respuestas se hace cuando tenemos el modelo, que es en el "Get".
        /*
         * El código del controlador que programaste en el laboratorio anterior ya es capaz de recoger 
         *  automáticamente estos datos al coincidir el nombre del control (responseTo)
         *  con la propiedad de la clase Message que tiene el mismo nombre.
         *
        if (message.getResponseTo() != null) {
            return "redirect:message/" + message.getResponseTo().getId();
            //redirigimos a la vista del mensaje al que se responde.
        }else if (message.getResponseTo() == null){
            return "redirect:message/" + message.getId();
            //redirigimos a la vista del mensaje que se acaba de publicar.
        }
        return "redirect:message/" + message.getId(); //por si acaso, vamos a la vista del mensaje que hemos publicado.
    }
    */
    
    //Nuevo metodo ej 6 p8: Controladores para seguir y dejar de seguir a usuarios.
    //Hará que el usuario asociado a la sesión actual siga a otro usuario. Este último se recibirá como un parámetro en la URL:
    @PostMapping(path = "/follow/{userId}")
    public String follow(@PathVariable("userId") int followedUserId, Principal principal) {
        Optional<User> followed = userRepository.findById(followedUserId);
        User currentUser = userRepository.findByEmail(principal.getName());
        if (!followed.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Follower not found");
        }try{
            userService.follow(currentUser, followed.get());
            return "redirect:/user/" + followedUserId;
        }catch(UserServiceException ex){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Seguidor ya sigue al usuario loggeado");
        }
    }
    @PostMapping(path = "/unfollow/{userId}")
    public String unfollow(@PathVariable("userId") int unfollowedUserId, Principal principal) {
        Optional<User> unfollower = userRepository.findById(unfollowedUserId);
        User currentUser = userRepository.findByEmail(principal.getName());
        if (!unfollower.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Follower not found");
        } try {
            userService.unfollow(currentUser, unfollower.get());
        } catch(UserServiceException ex) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Seguidor no sigue ya al usuario loggeado");
        }
        return "redirect:/user/" + unfollowedUserId;
    }

}
