package es.uc3m.musicfinder.controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import java.security.Principal;
import java.time.LocalDateTime;
// import java.time.ZoneId;
// import java.time.format.DateTimeFormatter;

// import jakarta.servlet.http.HttpServletRequest;

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

// Signup
import org.springframework.validation.BindingResult;
import jakarta.validation.Valid;

// Model & Services
import es.uc3m.musicfinder.model.*;
import es.uc3m.musicfinder.services.*;

// Pagination
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


@Controller
@RequestMapping(path = "/")
public class MainController {

    @Autowired
    private UserService userService;

    // Repositories
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private RecommendationRepository recommendationRepository;

    @Autowired
    private BlockRepository blockRepository;

    // MAIN VIEW -----------------------------------------------------------------------
    @GetMapping(path = "/")
    public String mainView(Model model, Principal principal, @RequestParam(value = "page", defaultValue = "0") int page) {
        // Check login status for navbar
        if (principal != null) {
            // If logged in, retrieve the current user and load recommendations
            String username = principal.getName(); // Get the username from Principal
            User currentUser = userRepository.findByUsername(username);
            String role = currentUser.getRole();
            model.addAttribute("role", role);

            // Check if the user has created any events regardless of current role
            List<Event> events = eventRepository.findAllByOrderByTimestampDesc();
            for (Event event : events) {
                if (event.getCreator().equals(currentUser)) {
                    model.addAttribute("createdEvent", true);
                }
            }

            // List<Event> userEvents = currentUser.getFavoriteEvents();
            // model.addAttribute("userEvents", userEvents); // check in thyemleaf if userEvents is empty

            if (currentUser != null) {
                // Load all recommendations
                // List<Recommendation> recommendations = recommendationRepository.findByRecommendTo(currentUser);
                // TODO: ONLY LOAD RECENT RECOMMENDATIONS (10 most recent for example)
                List<Recommendation> recentRecommendations = recommendationRepository.findTopNRecommendationsExcludingBlockedUsers(currentUser, PageRequest.of(0, 10));
                
                if (recentRecommendations.isEmpty()) {
                    model.addAttribute("noRecommendations", true);
                } else {
                    model.addAttribute("noRecommendations", false);
                }

                // TODO: specific query for only grabbing non blocked recommendations
                // Check for blocked users and add to list
                List<Block> blockedUsers = blockRepository.findByBlocker(currentUser);
                
                // Clean recommendations from blocked users
                // if (!blockedUsers.isEmpty()) {
                //     for (Block block : blockedUsers) {
                //         recentRecommendations.removeIf(rec -> rec.getRecommender().equals(block.getBlocked()));
                //     }
                    
                //     // old copilot
                //     // List<Recommendation> recommendations = new ArrayList<Recommendation>();
                //     // for (Recommendation rec : recommendationRepository.findByRecommendTo(currentUser)) {
                //     //     if (!blockedUsers.contains(rec.getRecommender())) {
                //     //         recommendations.add(rec);
                //     //     }
                //     // }

                //     model.addAttribute("recommendations", recentRecommendations);
                // } else { // If the user has not blocked anyone, load all recommendations
                //     model.addAttribute("recommendations", recentRecommendations);
                // }
            } else { // Case where the user is null
                model.addAttribute("noRecommendations", true);
            }
        }
        
        // Ensure 'page' is a valid index and non-negative
        if (page < 0) {
            page = 0;
        }
        
        int itemsPerPage = 16; // Number of events per page
        // Create a Pageable object to specify the page and number of items per page
        Pageable pageable = PageRequest.of(page, itemsPerPage);

        // Retrieve the desired page of events
        Page<Event> eventPage = eventRepository.findAllByOrderByTimestampDesc(pageable);

        model.addAttribute("events", eventPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", eventPage.getTotalPages());

        // TODO: maybe change to only if not blocked
        // pero no lo pide expresamente en el enunciado
        // Regardless, always load all events by timestamp
        // List<Event> events = eventRepository.findAllByOrderByTimestampDesc();

        // Check if the user has created any events regardless of current role
        // for (Event event : events) {
        //     String username = principal.getName(); // Get the username from Principal
        //     User currentUser = userRepository.findByUsername(username);
        //     if (event.getCreator().equals(currentUser)) {
        //         model.addAttribute("createdEvent", true);
        //     }
        // }

        // model.addAttribute("events", events);

        return "home"; // Return the home view
    }

    // ERROR & FORBIDDEN ----------------------------------------------------------------
    @GetMapping(path = "/error")
    public String errorView(Model model, Principal principal) {
        // Check login status to mainatain a consistent navbar
        if (principal != null) {
            String username = principal.getName();
            User currentUser = userRepository.findByUsername(username);
            String role = currentUser.getRole();
            model.addAttribute("role", role);
        }
        return "error";
    }
    @GetMapping(path = "/forbidden")
    public String forbiddenView(Model model, Principal principal) {
        if (principal == null) {
            model.addAttribute("error", "not_logged_in");
        }
        // Check login status to mainatain a consistent navbar
        if (principal != null) {
            String username = principal.getName();
            User currentUser = userRepository.findByUsername(username);
            String role = currentUser.getRole();
            model.addAttribute("role", role);
            model.addAttribute("error", "not_authorized");
        }

        return "forbidden";
    }

    // LOGIN & SIGNUP ------------------------------------------------------------------
    @GetMapping(path = "/login")
    public String loginForm() {
        return "login";
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
        if (userRepository.findByUsername(user.getUsername()) != null) {
            return "redirect:signup?username_already_registered";
        }
        if (!user.getPassword().equals(passwordRepeat)) {
            return "redirect:signup?password_mismatch";
        }
        user.setRole("USER"); // Set the role to USER as default
        userService.register(user);
        return "redirect:login?registered_succesfully";
    }

    // EVENT ---------------------------------------------------------------------------
    @GetMapping(path = "/event")
    public String eventView(Model model) {
        return "event";
    }

    @GetMapping(path = "/event/{eventID}")
    public String eventView(@PathVariable int eventId, Model model, Principal principal) {
        // Check login status for navbar
        if (principal != null) {
            String username = principal.getName();
            User currentUser = userRepository.findByUsername(username);
            String role = currentUser.getRole();
            model.addAttribute("role", role);
        }
        // Retrieve the event by its ID
        Optional<Event> optionalEvent = eventRepository.findById(eventId);

        // Handle the case where the event does not exist
        if (optionalEvent.isEmpty()) {
            model.addAttribute("error", "Event not found.");
            return "event_not_found"; // This could be a custom error page or a redirect
        }

        // If the event exists, add it to the model
        Event event = optionalEvent.get();
        model.addAttribute("event", event);

        // You can add additional information to the model as needed
        return "event"; // This should be the name of the Thymeleaf template for displaying the event
    }

    // // Ej 3 P7: New code for the controller method of the message view:
    // @GetMapping(path = "/message/{messageId}")
    // public String messageView(@PathVariable int messageId, Model model, Principal principal) {

    //     Optional<Message> messageOpt = messageRepository.findById(messageId);

    //     if (!messageOpt.isPresent()) {
    //         throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Message not found");
    //     }
    //     //Ej 3 p8: ocultación de mensajes de respuesta en vista de mensaje.
    //     if(messageOpt.get().getResponseTo() != null) {
    //         //Si el mensaje es una respuesta, no lo mostramos y devolvemos prohibido.
    //         throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Message is a response");
    //     }

    //     List<Message> messages = messageRepository.findByResponseToOrderByTimestampAsc(messageOpt.get());
    //     //! Duda: porqué no hacemos lo de optional con esto también?
    //     // if(messages !=null){//Si no hay respuestas, no las añadimos (por si acaso).
    //     //     model.addAttribute("respuestas", messages);
    //     // }
    //     model.addAttribute("respuestas", messages);//Aunque no haya respuestas, se añade la lista vacía.
    //     //Posible mejora: no poder responder a mensajes que son tuyos (sería teniendo "principal" en el método y viendo si el mensaje que 
    //     //se está viendo es del usuario loggeado).
    //     /*
    //     * currentUser = userRepository.findByEmail(principal.getName());
    //     * if(currentUser.equals(messageOpt.get().getUser())){
    //     * is_owner = 'mine';
    //     * Y luego haríamos como el if en el html para que no se muestre el botón de responder ni tampoco el formulario de respuesta.
    //     */
    //     model.addAttribute("message", messageOpt.get());
    //     return "message_view";
    // }

    @GetMapping(path = "/my_favorite_events")
    public String myFavoriteEventsView(Model model, Principal principal) {
        // Check login status
        if (principal == null) {
            return "redirect:/forbidden?not_logged_in";
        }
        // If logged in, retrieve the current user 
        String username = principal.getName();
        User currentUser = userRepository.findByUsername(username);
        String role = currentUser.getRole();
        model.addAttribute("role", role);

        // Load favorite events
        List<Event> favoriteEvents = currentUser.getFavoriteEvents();
        model.addAttribute("favoriteEvents", favoriteEvents);
        return "my_favorites";
    }
    @PostMapping(path = "/add_to_favorites")
    public String addToFavorites(@RequestParam("eventId") int eventId, Principal principal) {
        // Check login status
        if (principal == null) {
            return "redirect:/forbidden?not_logged_in";
        }
        // If logged in, retrieve the current user 
        String username = principal.getName();
        User currentUser = userRepository.findByUsername(username);
        String role = currentUser.getRole();

        // Retrieve the event by its ID
        Optional<Event> optionalEvent = eventRepository.findById(eventId);

        // Handle the case where the event does not exist
        if (optionalEvent.isEmpty()) {
            return "redirect:/event_not_found";
        }

        // If the event exists, add it to the user's favorite events
        Event event = optionalEvent.get();
        List<Event> favoriteEvents = currentUser.getFavoriteEvents();
        favoriteEvents.add(event);
        currentUser.setFavoriteEvents(favoriteEvents);
        userRepository.save(currentUser);

        // return "redirect:/event/" + eventId + "?added_to_favorites";
        return "redirect:/my_favorite_events";
    }

    @GetMapping(path = "/my_recommendations")
    public String myRecommendationsView(Model model, Principal principal) {
        // Check login status
        if (principal == null) {
            return "redirect:/forbidden?not_logged_in";
        }
        // If logged in, retrieve the current user 
        String username = principal.getName();
        User currentUser = userRepository.findByUsername(username);
        String role = currentUser.getRole();
        model.addAttribute("role", role);

        // Load recommendations
        List<Recommendation> recommendations = recommendationRepository.findByRecommenderOrderByTimestampDesc(currentUser);
        model.addAttribute("recommendations", recommendations);
        return "my_recommendations";
    }

    @GetMapping(path = "/create_event")
    public String createEventView(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/forbidden?not_logged_in";
        }
        if (principal != null) {
            User user = userRepository.findByUsername(principal.getName());
            String role = user.getRole();
            if (!(role.equals("ORGANIZER") || role.equals("ADMIN"))) {
                return "redirect:/forbidden?not_authorized";
            }
        }
        // model.addAttribute("event", new Event());
        return "create_event";
    }
    // @PostMapping(path = "/event/create_event")
    // public String createEvent(@ModelAttribute Event event, Principal principal) {
    //     // Check login status
    //     if (principal == null) {
    //         return "redirect:/forbidden?not_logged_in";
    //     }
    //     if (principal != null) {
    //         User user = userRepository.findByUsername(principal.getName());
    //         String role = user.getRole();
    //         if (!(role.equals("ORGANIZER") || role.equals("ADMIN"))) {
    //             return "redirect:/forbidden?not_authorized_to_create_events";
    //         }
    //     }

    //     // event.setUser(user);
    //     // event.setTimestamp(new Date());
    //     // eventRepository.save(event);

    //     return "redirect:/event/" + event.getId() + "?succesfully_created";
    // }
    @PostMapping(path = "/create_event")
    public String createEvent(@ModelAttribute Event event, Principal principal) {
            // RedirectAttributes redirectAttributes
            // , HttpServletRequest request) {
        // Check login status to avoid allowing non-logged in users to create events
        /*
        if (principal == null) {
            redirectAttributes.addFlashAttribute("error", "You must be logged in to create an event.");
            return "redirect:/forbidden?not_logged_in";
        }

        // Get the current user
        User user = userRepository.findByUsername(principal.getName());

        // Ensure the user is allowed to create events (like 'ORGANIZER' or 'ADMIN')
        if (!(user.getRole().equals("ORGANIZER") || user.getRole().equals("ADMIN"))) {
            redirectAttributes.addFlashAttribute("error", "You are not authorized to create events.");
            return "redirect:/forbidden";
        }
        */
        if (principal == null) {
            return "redirect:/forbidden?not_logged_in";
        }
        User user = userRepository.findByUsername(principal.getName());
        String role = user.getRole();
        if (!(role.equals("ORGANIZER") || role.equals("ADMIN"))) {
            return "redirect:/forbidden?not_authorized_to_create_events";
        }

        event.setCreator(user); // Set the creator to the current user
        event.setTimestamp(new Date()); // Set the current timestamp

        // // Extract the date and time from the request
        // String dateStr = request.getParameter("date");
        // String timeStr = request.getParameter("time");

        // // Combine them into a single DateTime string
        // String combinedDateTimeStr = dateStr + " " + timeStr;

        // // Parse into a Date object
        // DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        // LocalDateTime localDateTime = LocalDateTime.parse(combinedDateTimeStr, formatter);
        // Date date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        // event.setDate(date);


        // Save the event to the database
        Event savedEvent = eventRepository.save(event);

        // redirectAttributes.addFlashAttribute("success", "Event created successfully.");
        return "redirect:/event/" + savedEvent.getId() + "?succesfully_created";
    }
    // copilot
    // @PostMapping("/create_event")
    // public String createEvent(@RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date date,
    //                           @RequestParam("time") @DateTimeFormat(pattern = "HH:mm") String time,
    //                           Event event, Principal principal) {
    //     if (principal == null) {
    //         return "redirect:/forbidden?not_logged_in";
    //     }

    //     User user = userRepository.findByUsername(principal.getName());
    //     String role = user.getRole();
    //     if (!(role.equals("ORGANIZER") || role.equals("ADMIN"))) {
    //         return "redirect:/forbidden?not_authorized_to_create_events";
    //     }
        
    //     try {
    //         SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
    //         Date timePart = dateFormat.parse(time);
    //         Calendar timeCalendar = Calendar.getInstance();
    //         timeCalendar.setTime(timePart);
    //         int hours = timeCalendar.get(Calendar.HOUR_OF_DAY);
    //         int minutes = timeCalendar.get(Calendar.MINUTE);

    //         Calendar eventCalendar = Calendar.getInstance();
    //         eventCalendar.setTime(date);
    //         eventCalendar.set(Calendar.HOUR_OF_DAY, hours);
    //         eventCalendar.set(Calendar.MINUTE, minutes);

    //         event.setDate(eventCalendar.getTime());
    //     } catch (ParseException e) {
    //         e.printStackTrace();
    //     }

    //     event.setCreator(user); // Set the creator to the current user
    //     event.setTimestamp(new Date()); // Set the current timestamp
    
    //     Event savedEvent = eventRepository.save(event);

    //     // redirectAttributes.addFlashAttribute("success", "Event created successfully.");
    //     return "redirect:/event/" + savedEvent.getId() + "?succesfully_created";
    // }

    @GetMapping(path = "/recommended")
    public String recommendedView(Model model, Principal principal) {
        // Check login status
        if (principal == null) {
            return "redirect:/forbidden?not_logged_in";
        }
        // If logged in, retrieve the current user 
        String username = principal.getName();
        User currentUser = userRepository.findByUsername(username);
        String role = currentUser.getRole();
        model.addAttribute("role", role);

        // Load recommendations
        List<Recommendation> recommendations = recommendationRepository.findRecommendationsExcludingBlockedUsers(currentUser);
        model.addAttribute("recommendations", recommendations);
        return "recommended";
    }


    @GetMapping(path = "/admin_view")
    public String admin_view(Model model, Principal principal) {
        // Check login status
        if (principal == null) {
            return "redirect:/forbidden?not_logged_in";
        }
        // If logged in, retrieve the current user 
        User user = userRepository.findByUsername(principal.getName());
        String role = user.getRole();
        //solo si es adming puede ver las estadisticas
        if (!(role.equals("ADMIN"))) {
            return "redirect:/forbidden?not_authorized_to_view_admin_view";
        }
        return "admin_view";
    }

    @GetMapping("/data_dashboard")
    public String dataDashboard(Model model, Principal principal) {
        // Check login status
        if (principal == null) {
            return "redirect:/forbidden?not_logged_in";
        }
        // If logged in, retrieve the current user 
        User user = userRepository.findByUsername(principal.getName());
        String role = user.getRole();
        //solo si es adming puede ver las estadisticas
        if (!(role.equals("ADMIN"))) {
            return "redirect:/forbidden?not_authorized_to_view_data_dashboard";
        }

        // Add the username to the model
        model.addAttribute("username", user.getUsername());

        // Obtener el número de recomendaciones para el usuario actual
        int recommendationToUserCount = recommendationRepository.countRecommendationsFromFriends(user);
        model.addAttribute("recommendationToUserCount", recommendationToUserCount);

        // Obetener el numero de recomendaciones realizadas por el usuario actual
        int recommendationFromUserCount = recommendationRepository.countRecommendationsToFriends(user);
        model.addAttribute("recommendationFromUserCount", recommendationFromUserCount);

        // Obtener el número de usuarios bloqueados por el usuario actual
        int blockedUserCount = blockRepository.countBlockedUsers(user);
        model.addAttribute("blockedUserCount", blockedUserCount);

        // Obtener el número de usuarios que han bloqueado al usuario actual
        int UsersBlockingUserCount = blockRepository.countUsersBlockingUser(user);
        model.addAttribute("usersBlockingUserCount", blockedUserCount);

        // Obtener el número de eventos creados por el usuario actual
        int eventCount = eventRepository.countEventsCreatedByUser(user);
        model.addAttribute("eventCount", eventCount);

        // Obtener el número de eventos marcados como favoritos por el usuario actual
        // int favoriteEventCount = eventRepository.countFavoriteEventsForUser(user);
        int favoriteEventCount = user.getFavoriteEvents().size();
        model.addAttribute("favoriteEventCount", favoriteEventCount);
    return "data_dashboard";
    }




    // //Nuevo metodo para ruta /post
    // @PostMapping(path = "/post")
    // public String postMessage(@ModelAttribute Message message, Principal principal) {
    //     User user = userRepository.findByEmail(principal.getName());
    //     message.setUser(user);
    //     message.setTimestamp(new Date());
    //     messageRepository.save(message);
    //     //Lo de las respuestas se hace cuando tenemos el modelo, que es en el "Get".
    //     /*
    //     * El código del controlador que programaste en el laboratorio anterior ya es capaz de recoger 
    //     *  automáticamente estos datos al coincidir el nombre del control (responseTo)
    //     *  con la propiedad de la clase Message que tiene el mismo nombre.
    //     */
    //     if (message.getResponseTo() != null) {
    //         return "redirect:message/" + message.getResponseTo().getId();
    //         //redirigimos a la vista del mensaje al que se responde.
    //     }else if (message.getResponseTo() == null){
    //         return "redirect:message/" + message.getId();
    //         //redirigimos a la vista del mensaje que se acaba de publicar.
    //     }
    //     return "redirect:message/" + message.getId(); //por si acaso, vamos a la vista del mensaje que hemos publicado.
    // }


    // USER ---------------------------------------------------------------------------- ?????????????
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
        User currentUser = userRepository.findByUsername(principal.getName());
        // Using optional to avoid null pointer exceptions
        // its a method from the CrudRepository interface
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

    //Nuevo metodo ej 6 p8: Controladores para seguir y dejar de seguir a usuarios.
    //Hará que el usuario asociado a la sesión actual siga a otro usuario. Este último se recibirá como un parámetro en la URL:
    @PostMapping(path = "/follow/{userId}")
    public String follow(@PathVariable("userId") int followedUserId, Principal principal) {
        Optional<User> followed = userRepository.findById(followedUserId);
        User currentUser = userRepository.findByUsername(principal.getName());
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
        User currentUser = userRepository.findByUsername(principal.getName());
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
