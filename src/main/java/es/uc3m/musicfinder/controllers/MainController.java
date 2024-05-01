package es.uc3m.musicfinder.controllers;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.data.domain.PageRequest;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.validation.BindingResult;
import jakarta.validation.Valid;

import es.uc3m.musicfinder.model.User;
import es.uc3m.musicfinder.model.UserRepository;
import es.uc3m.musicfinder.services.UserService;
import es.uc3m.musicfinder.services.UserServiceException;

import es.uc3m.musicfinder.model.Message;
import es.uc3m.musicfinder.model.MessageRepository;


@Controller
@RequestMapping(path = "/")
public class MainController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private MessageRepository messageRepository;

    @GetMapping(path = "/")
    public String mainView(Model model, Principal principal) {
        // User current_user = userRepository.findByEmail(principal.getName());
        
        // List<Message> messages = messageRepository.messagesFromFollowedUsers(current_user, PageRequest.of(0, 10));

        // model.addAttribute("messages", messages);

        return "index";
    }

    @GetMapping(path = "/error")
    public String errorView() {
        return "404";
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
            return "redirect:signup?duplicate_email";
        }
        if (!user.getPassword().equals(passwordRepeat)) {
            return "redirect:signup?passwords";
        }
        userService.register(user);
        return "redirect:signin?succesfully_registered";
    }

    @GetMapping(path = "/signin")
    public String loginForm() {
        return "signin";
    }

    @GetMapping(path = "/user")
    public String userView(Model model) {
        List<Message> messages = new ArrayList<Message>();
        User user = new User();
        Message message = new Message();
    
        //Ejercicio 2: añadir otro mensaje nuevo.
        user = new User();
        user.setId(2);
        user.setEmail("luciabarranco2002@gmail.com");
        user.setName("Lucía");
        message = new Message();
        message.setId(2);
        message.setUser(user);
        message.setText("hellow, World!!");
        message.setTimestamp(new Date());
        messages.add(message);

        model.addAttribute("messages", messages);
        return "user";
    }
    @GetMapping(path = "/user/{userId}")
    public String userView(@PathVariable int userId, Model model, Principal principal) {
        String followButton = "";
        User current_user = userRepository.findByEmail(principal.getName());
        Optional<User> userOpt = userRepository.findById(userId);

        // para que nadie tenga info sobre el server que corre la app.
        if (!userOpt.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Message not found");
        }
        //Ej 3 p8: ocultación de mensajes de respuesta en vista de perfil de usuario.
        List<Message> messages = messageRepository.findByUserAndResponseToIsNullOrderByTimestampDesc(userOpt.get());
        model.addAttribute("user", userOpt.get());
        model.addAttribute("messages", messages);

        // Ej 7 p8 (valor de la cadena "followButton")----------------------
        if(current_user.equals(userOpt.get())) {
            followButton = "none";
        }
        if(userService.follows(current_user, userOpt.get())) {
            followButton = "unfollow";
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
             * current_user = userRepository.findByEmail(principal.getName());
             * if(current_user.equals(messageOpt.get().getUser())){
             * is_owner = 'mine';
             * Y luego haríamos como el if en el html para que no se muestre el botón de responder ni tampoco el formulario de respuesta.
             */
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
         */
        if (message.getResponseTo() != null) {
            return "redirect:message/" + message.getResponseTo().getId();
            //redirigimos a la vista del mensaje al que se responde.
        }else if (message.getResponseTo() == null){
            return "redirect:message/" + message.getId();
            //redirigimos a la vista del mensaje que se acaba de publicar.
        }
        return "redirect:message/" + message.getId(); //por si acaso, vamos a la vista del mensaje que hemos publicado.
    }

    //Nuevo metodo ej 6 p8: Controladores para seguir y dejar de seguir a usuarios.
    //Hará que el usuario asociado a la sesión actual siga a otro usuario. Este último se recibirá como un parámetro en la URL:
    @PostMapping(path = "/follow/{userId}")
    public String follow(@PathVariable("userId") int followedUserId, Principal principal){
        Optional<User> followed = userRepository.findById(followedUserId);
        User current_user = userRepository.findByEmail(principal.getName());
        if (!followed.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Follower not found");
        }try{
            userService.follow(current_user, followed.get());
            return "redirect:/user/" + followedUserId;
        }catch(UserServiceException ex){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Seguidor ya sigue al usuario loggeado");
        }
    }
    @PostMapping(path = "/unfollow/{userId}")
    public String unfollow(@PathVariable("userId") int unfollowedUserId, Principal principal) {
        Optional<User> unfollower = userRepository.findById(unfollowedUserId);
        User current_user = userRepository.findByEmail(principal.getName());
        if (!unfollower.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Follower not found");
        } try {
            userService.unfollow(current_user, unfollower.get());
        } catch(UserServiceException ex) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Seguidor no sigue ya al usuario loggeado");
        }
        return "redirect:/user/" + unfollowedUserId;
    }

}
