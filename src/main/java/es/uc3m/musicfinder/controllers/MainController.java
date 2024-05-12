package es.uc3m.musicfinder.controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.beans.BeanProperty;
import java.security.Principal;
import java.time.LocalDateTime;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ModelAttribute;

import org.springframework.web.server.ResponseStatusException;

import org.springframework.http.HttpStatus;

import org.springframework.data.domain.PageRequest;

import org.springframework.beans.factory.annotation.Autowired;

// Signup
import org.springframework.validation.BindingResult;
import jakarta.validation.Valid;

// Model & Services
import es.uc3m.musicfinder.model.*;
import es.uc3m.musicfinder.services.*;

// Pagination
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

// Redirect
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// Transactional (for unblocking)
import org.springframework.transaction.annotation.Transactional;


@Controller
@RequestMapping(path = "/")
public class MainController {

    // Services
    @Autowired
    private UserService userService;

    @Autowired
    private EventService eventService;

    @Autowired
    private RecommendationService recommendationService;

    @Autowired
    private BlockService blockService;

    // Repositories
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private RecommendationRepository recommendationRepository;

    @Autowired
    private BlockRepository blockRepository;



    // MAIN VIEW ------------------------------------------------------------------------------------------------------------------------------------------------------
    @GetMapping(path = "/")
    public String mainView(Model model, Principal principal, @RequestParam(value = "page", defaultValue = "0") int page) {
        // Load events for everyone
        List<Event> events = eventRepository.findAllByOrderByTimestampDesc();
        model.addAttribute("events", events);
        
        // Check login status for navbar
        if (principal != null) {
            // If logged in, retrieve the current user and load recommendations
            String username = principal.getName(); // Get the username from Principal
            User currentUser = userRepository.findByUsername(username);
            String role = currentUser.getRole();
            model.addAttribute("username", username);
            model.addAttribute("role", role);

            // Check if the user has created any events
            List<Event> userEvents = eventRepository.findByCreatorOrderByTimestampDesc(currentUser);
            if (!userEvents.isEmpty())
                model.addAttribute("createdEvent", true);

            for (Event event : events) {
                if (event.getCreator().equals(currentUser)) {
                }
                // Check if the current user has this event in favorites
                boolean isFavorite = currentUser.getFavoriteEvents().contains(event);
                model.addAttribute("isFavorite"+event.getId(), isFavorite); // Boolean to indicate if the event is favorited
            }

            if (currentUser != null) {
                // ONLY LOAD RECENT RECOMMENDATIONS (10 most recent for example)
                // Recomendations always exclude blocked users
                List<Recommendation> recentRecommendations = recommendationRepository.findTopNRecommendationsExcludingBlockedUsers(currentUser, PageRequest.of(0, 10));
                
                if (recentRecommendations.isEmpty()) {
                    model.addAttribute("noRecommendations", true);
                } else {
                    for (Recommendation recommendation : recentRecommendations) {
                        // Check if the current user has this event in favorites
                        boolean isFavorite = currentUser.getFavoriteEvents().contains(recommendation.getEvent());
                        model.addAttribute("isFavorite"+recommendation.getEvent().getId(), isFavorite); // Boolean to indicate if the event is favorited
                    }
                    model.addAttribute("noRecommendations", false);
                }
                
                model.addAttribute("recommendations", recentRecommendations);
            }
        }
        return "home"; // Return the home view
    }



    // ERROR & FORBIDDEN --------------------------------------------------------------------------------------------------------------------------------
    @GetMapping(path = "/error")
    public String errorView(Model model, Principal principal) {
        // Check login status to mainatain a consistent navbar
        if (principal != null) {
            String username = principal.getName();
            User currentUser = userRepository.findByUsername(username);
            String role = currentUser.getRole();
            // Check if the user has created any events
            List<Event> userEvents = eventRepository.findByCreatorOrderByTimestampDesc(currentUser);
            if (!userEvents.isEmpty())
                model.addAttribute("createdEvent", true);
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
            // Check if the user has created any events
            List<Event> userEvents = eventRepository.findByCreatorOrderByTimestampDesc(currentUser);
            if (!userEvents.isEmpty())
                model.addAttribute("createdEvent", true);
            model.addAttribute("role", role);
            model.addAttribute("error", "not_authorized");
        }
        return "forbidden";
    }



    // LOGIN & SIGNUP --------------------------------------------------------------------------------------------------------------------------------------    
    @GetMapping(path = "/login")
    public String loginForm() {
        return "login";
    }
    // // TODO @{/customLogout} see event and home in the navbar for the potential implementation
    // @PostMapping(path = "/logout")
    // public String logout(@RequestParam(name = "returnLogOutUrl", required = false) String returnUrl) {
    //     // Creating a new URL without keeping previous query parameters
    //     String redirectUrl = (returnUrl != null && !returnUrl.isEmpty()) ? returnUrl : "/";

    //     String queryParam = "?_logged_out";

    //     // Construct the clean URL
    //     // Spliting at first ? to ensure previous query parameters are removed
    //     String finalRedirect = "redirect:" + redirectUrl.split("\\?")[0] + queryParam;

    //     return finalRedirect;
    // }

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

    @GetMapping(path = "/user")
    public String userView(Model model, Principal principal) {
        // Check login status
        if (principal == null) {
            return "redirect:/forbidden?not_logged_in";
        }
        // If logged in, retrieve the current user 
        String username = principal.getName();
        User currentUser = userRepository.findByUsername(username);
        String role = currentUser.getRole();
        // Check if the user has created any events
        List<Event> userEvents = eventRepository.findByCreatorOrderByTimestampDesc(currentUser);
        if (!userEvents.isEmpty())
            model.addAttribute("createdEvent", true);
        
        model.addAttribute("username", username);
        model.addAttribute("role", role);

        // Email del usuario
        model.addAttribute("email", currentUser.getEmail());

        // Numero de bloqueos realizados por el usuario
        int blockedUserCount = blockRepository.countBlockedUsers(currentUser);
        model.addAttribute("blockedUserCount", blockedUserCount);

        // Número de recomendaciones que ha recibido el usuario
        int recommendationToUserCount = recommendationRepository.countRecommendationsFromFriendsExcludingBlokedUsers(currentUser);
        model.addAttribute("recommendationToUserCount", recommendationToUserCount);

        // Número de recomendaciones que ha hecho el usuario
        int recommendationFromUserCount = recommendationRepository.countRecommendationsToFriends(currentUser);
        model.addAttribute("recommendationFromUserCount", recommendationFromUserCount);

        // Numero de eventos favoritos del usuario
        int favoriteEventCount = currentUser.getFavoriteEvents().size();
        model.addAttribute("favoriteEventCount", favoriteEventCount);

        // Numero de eventos creados por el usuario
        int eventCount = eventRepository.countEventsCreatedByUser(currentUser);
        model.addAttribute("eventCount", eventCount);

        // Load blocked users
        List<User> blockedUsers = blockService.getBlockedUsers(currentUser);
        model.addAttribute("blockedUsers", blockedUsers);

        return "user"; // Same view for all users
    }



    // EVENT ----------------------------------------------------------------------------------------------------------------------------------------------
    @GetMapping(path = "/event/{eventId}")
    public String eventView(@PathVariable("eventId") int eventId, Model model, Principal principal) {
        // Retrieve the event by its ID
        Optional<Event> optionalEvent = eventRepository.findById(eventId);

        // Handle the case where the event does not exist
        if (optionalEvent.isEmpty()) {
            model.addAttribute("error", "Event not found.");
            return "/404?event_not_found";
        }

        // If the event exists, add it to the model
        Event event = optionalEvent.get();
        model.addAttribute("event", event);
        
        // Check login status for navbar
        if (principal != null) {
            String username = principal.getName();
            User currentUser = userRepository.findByUsername(username);
            String role = currentUser.getRole();
            model.addAttribute("role", role);

            // Check if the user has created any events for the navbar
            List<Event> userEvents = eventRepository.findByCreatorOrderByTimestampDesc(currentUser);
            if (!userEvents.isEmpty())
                model.addAttribute("createdEvent", true);
            
            // Check if the current user has this event in favorites
            boolean isFavorite = currentUser.getFavoriteEvents().contains(event);
            model.addAttribute("isFavorite", isFavorite); // Boolean to indicate if the event is favorited

            // if the current user has blocked the creator
            if (blockRepository.existsByBlockerAndBlocked(currentUser, event.getCreator()))
                model.addAttribute("creatorBlocked", true);
            else
                model.addAttribute("creatorBlocked", false);
        }

        return "event";
    }

    @GetMapping(path = "/my_events")
    public String myEventsView(Model model, Principal principal) {
        // Check login status
        if (principal == null) {
            return "redirect:/forbidden?not_logged_in";
        }
        // If logged in, retrieve the current user 
        String username = principal.getName();
        User currentUser = userRepository.findByUsername(username);
        String role = currentUser.getRole();
        model.addAttribute("role", role);

        // Load events created by the current user
        List<Event> userEvents = eventRepository.findByCreatorOrderByTimestampDesc(currentUser);
        model.addAttribute("userEvents", userEvents);
        // Check if the user has created any events for the navbar
        if (!userEvents.isEmpty())
            model.addAttribute("createdEvent", true);

        return "my_events";
    }

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
        // Check if the user has created any events for the navbar
        List<Event> userEvents = eventRepository.findByCreatorOrderByTimestampDesc(currentUser);
        if (!userEvents.isEmpty())
            model.addAttribute("createdEvent", true);

        // Load favorite events
        List<Event> favoriteEvents = currentUser.getFavoriteEvents();
        if (favoriteEvents.isEmpty()) {
            model.addAttribute("noFavoriteEvents", true);
        } else {
            model.addAttribute("noFavoriteEvents", false);
        }
        model.addAttribute("favoriteEvents", favoriteEvents);
        return "my_favorites";
    }

    @GetMapping(path = "/create_event")
    public String createEventView(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/forbidden?not_logged_in";
        }
        User user = userRepository.findByUsername(principal.getName());
        String role = user.getRole();
        if (!(role.equals("ORGANIZER"))) {
            return "redirect:/forbidden?not_authorized";
        }
        // Check if the user has created any events
        List<Event> userEvents = eventRepository.findByCreatorOrderByTimestampDesc(user);
        if (!userEvents.isEmpty())
            model.addAttribute("createdEvent", true);
        model.addAttribute("role", role);
        
        return "create_event";
    }

    @PostMapping(path = "/create_event") 
    public String createEvent(@ModelAttribute Event event, Principal principal) {
        if (principal == null) {
            return "redirect:/forbidden?not_logged_in";
        }
        User user = userRepository.findByUsername(principal.getName());
        String role = user.getRole();
        if (!(role.equals("ORGANIZER"))) {
            return "redirect:/forbidden?not_authorized_to_create_events";
        }

        event.setTimestamp(new Date()); // Set the current timestamp
        event.setCreator(user); // Set the creator to the current user

        // Save the event to the database
        Event savedEvent = eventRepository.save(event);

        return "redirect:/event/" + savedEvent.getId() + "?succesfully_created";
    }

    @PostMapping(path = "/delete_event")
    @Transactional // If any exception occurs, the transaction will be rolled back
    public String deleteEvent(@RequestParam("eventId") int eventId,
                            @RequestParam(name = "returnDeleteUrl", required = false) String returnDeleteUrl, Principal principal) {
        if (principal == null) {
            return "redirect:/forbidden?not_logged_in";
        }
        User user = userRepository.findByUsername(principal.getName());
        String role = user.getRole();
        if (!(role.equals("ORGANIZER"))) {
            return "redirect:/forbidden?not_authorized_to_delete_events";
        }

        // Validate event existence
        Optional<Event> eventOpt = eventRepository.findById(eventId);
        if (!eventOpt.isPresent()) {
            return "redirect:/error?event_not_found";
        }

        Event event = eventOpt.get();
        String eventToDeleteName = event.getName();
        try {
            eventRepository.deleteById(eventId); // Delete the event from the database
        } catch (Exception e) {
            return "redirect:/error?failed_to_delete_event";
        }

        // Creating a new URL without keeping previous query parameters
        // Variable (condition) ? value_if_true : value_if_false
        String redirectDeleteUrl = (returnDeleteUrl != null && !returnDeleteUrl.isEmpty()) ? returnDeleteUrl : "/my_events";

        String queryParam = "?__event=" + eventToDeleteName + "__deleted_succesfully";

        // Construct the clean URL
        // Spliting at first ? to ensure previous query parameters are removed
        String finalRedirect = "redirect:" + redirectDeleteUrl.split("\\?")[0] + queryParam;

        return finalRedirect;
    }



     // RECOMENDACIONES -------------------------------------------------------------------------------------------------------------------------------------------
    @GetMapping(path = "/recommended")
    public String recommendedView(Model model, Principal principal) {
        // Check login status
        if (principal == null) {
            return "redirect:/forbidden?not_logged_in";
        }

        // Check login status for navbar
        String username = principal.getName();         // If logged in, retrieve the current user 
        User currentUser = userRepository.findByUsername(username);
        String role = currentUser.getRole();
        model.addAttribute("role", role);

        // Check if the user has created any events
        List<Event> userEvents = eventRepository.findByCreatorOrderByTimestampDesc(currentUser);
        if (!userEvents.isEmpty())
            model.addAttribute("createdEvent", true);
        
        if (principal != null) {
            if (currentUser != null) {
                // LOAD ALL RECOMMENDATIONS (not pageable here)
                // Recomendations always exclude blocked users
                List<Recommendation> recentRecommendations = recommendationRepository.findRecommendationsExcludingBlockedUsers(currentUser);
                model.addAttribute("recommendations", recentRecommendations);

                if (recentRecommendations.isEmpty()) {
                    model.addAttribute("noRecommendations", true);
                } else {
                    for (Recommendation recommendation : recentRecommendations) {
                        // Check if the current user has this event in favorites (if logged in)
                        boolean isFavorite = currentUser.getFavoriteEvents().contains(recommendation.getEvent());
                        model.addAttribute("isFavorite"+recommendation.getEvent().getId(), isFavorite); // Boolean to indicate if the event is favorited
                    }
                    model.addAttribute("noRecommendations", false);
                }
            }
        }
        return "recommended";
    }

    @GetMapping(path = "/my_recommendations")
    public String myRecommendationsView(Model model, Principal principal) {
        // Check login status
        if (principal == null) {
            return "redirect:/forbidden?not_logged_in";
        }

        String username = principal.getName();
        User currentUser = userRepository.findByUsername(username);
        String role = currentUser.getRole();
        model.addAttribute("role", role);

        // Check if the user has created any events
        List<Event> userEvents = eventRepository.findByCreatorOrderByTimestampDesc(currentUser);
        if (!userEvents.isEmpty())
            model.addAttribute("createdEvent", true);

        // Load all the recommendations made by the current user
        List<Recommendation> recommendations = recommendationRepository.findByRecommenderOrderByTimestampDesc(currentUser);
        model.addAttribute("recommendations", recommendations);

        if (principal != null) {
            if (currentUser != null) {
                List<Recommendation> recentRecommendations = recommendationRepository.findByRecommenderOrderByTimestampDesc(currentUser);
                model.addAttribute("recommendations", recentRecommendations);

                if (recentRecommendations.isEmpty()) {
                    model.addAttribute("noRecommendations", true);
                } else {
                    for (Recommendation recommendation : recentRecommendations) {
                        // Check if the current user has this event in favorites (if logged in)
                        boolean isFavorite = currentUser.getFavoriteEvents().contains(recommendation.getEvent());
                        model.addAttribute("isFavorite"+recommendation.getEvent().getId(), isFavorite); // Boolean to indicate if the event is favorited
                    }
                    model.addAttribute("noRecommendations", false);
                }
            }
        }
        return "my_recommendations";
    }

    @PostMapping("/recommend_event")
    public String recommendEvent(@RequestParam("eventId") int eventId,
                                @RequestParam("username") String username,
                                @RequestParam(name = "returnRecUrl", required = false) String returnRecUrl,
                                Principal principal) {
        // Check login status
        if (principal == null) {
            return "redirect:/forbidden?not_logged_in";
        }

        // If logged in, retrieve the current user
        String currentUsername = principal.getName();
        User currentUser = userRepository.findByUsername(currentUsername);

        // Retrieve the user to recommend the event to by their username
        User userToRecommend = userRepository.findByUsername(username);

        // Check if the user to recommend the event to exists
        if (userToRecommend == null) {
            return "redirect:/error?user_to_recommend_event_not_found";
        }

        // Retrieve the event by its ID
        Optional<Event> optionalEvent = eventRepository.findById(eventId);

        // Handle the case where the event does not exist
        if (optionalEvent.isEmpty()) {
            return "redirect:/error?event_to_recommend_not_found";
        }

        // If the event exists, add a recommendation
        Event event = optionalEvent.get();

        //TODO: review recommend in UserServiceImpl (UserServiceException)

        // try {
        //     userService.recommend(userToRecommend, userToRecommend, event);
        // } catch (UserServiceException e) {
        //     return "redirect:/error?failed_to_recommend_event";
        // }
        
        // Check if the user is trying to recommend the event to themselves
        if (currentUser.equals(userToRecommend)) {
            return "redirect:/error?cannot_recommend_event_to_yourself";
        }

        // Check if the user has already recommended the event to the target user
        if (recommendationRepository.existsByRecommenderAndRecommendToAndEvent(currentUser, userToRecommend, event)) {
            // Construct the URL with a query parameter indicating the error
            String redirectUrl = "/event/" + eventId + "?event_already_recommended_to_user";

            // Redirect to the constructed URL
            return "redirect:" + redirectUrl;
        }

        Recommendation newRecommendation = new Recommendation();
        newRecommendation.setEvent(event);
        newRecommendation.setRecommender(currentUser);
        newRecommendation.setRecommendTo(userToRecommend);
        newRecommendation.setTimestamp(new Date());

        // Save the recommendation to the database
        recommendationRepository.save(newRecommendation);

        // return "redirect:/?__event_recommended_succesfully"; // old return

        // Creating a new URL without keeping previous query parameters
        // Variable (condition) ? value_if_true : value_if_false
        String redirectUrl = (returnRecUrl != null && !returnRecUrl.isEmpty()) ? returnRecUrl : "/event/" + eventId;

        String queryParam = "?__event=" + eventId + "__recommended_succesfully";

        // Construct the clean URL
        // Spliting at first ? to ensure previous query parameters are removed
        String finalRedirect = "redirect:" + redirectUrl.split("\\?")[0] + queryParam;

        return finalRedirect;
    }

    @PostMapping("/remove_recommendation")
    public String removeRecommendation(@RequestParam("recommendationId") int recommendationId,
                                    @RequestParam(name = "returnRemoveUrl", required = false) String returnRemoveUrl,
                                    Principal principal) {
        // Check login status
        if (principal == null) {
            return "redirect:/forbidden?not_logged_in";
        }

        // If logged in, retrieve the current user
        String currentUsername = principal.getName();
        User currentUser = userRepository.findByUsername(currentUsername);

        // Retrieve the recommendation by its ID
        Optional<Recommendation> optionalRecommendation = recommendationRepository.findById(recommendationId);

        // Handle the case where the recommendation does not exist
        if (optionalRecommendation.isEmpty()) {
            return "redirect:/error?recommendation_not_found";
        }

        // If the recommendation exists, remove it
        Recommendation recommendation = optionalRecommendation.get();

        // Check if the current user is the recommender
        if (!recommendation.getRecommender().equals(currentUser)) {
            return "redirect:/forbidden?you_couldnt_have_made_this_recommendation_to_yourself_haha";
        }

        // Delete the recommendation
        recommendationRepository.deleteById(recommendationId);

        // Creating a new URL without keeping previous query parameters
        // Variable (condition) ? value_if_true : value_if_false
        String redirectUrl = (returnRemoveUrl != null && !returnRemoveUrl.isEmpty()) ? returnRemoveUrl : "/my_recommendations";

        String queryParam = "?__recommendation_removed_succesfully";

        // Construct the clean URL
        // Spliting at first ? to ensure previous query parameters are removed
        String finalRedirect = "redirect:" + redirectUrl.split("\\?")[0] + queryParam;

        return finalRedirect;
    }



    // ADMIN PANEL --------------------------------------------------------------------------------------------------------------------------------------

    @GetMapping(path = "/admin_panel")
    public String admin_view(Model model, Principal principal) {
        // Check login status
        if (principal == null) {
            return "redirect:/forbidden?not_logged_in";
        }
        // If logged in, retrieve the current user 
        User user = userRepository.findByUsername(principal.getName());
        String role = user.getRole();
        // Check if the user has created any events
        List<Event> userEvents = eventRepository.findByCreatorOrderByTimestampDesc(user);
        if (!userEvents.isEmpty())
            model.addAttribute("createdEvent", true);
            
        //solo si es adming puede ver las estadisticas
        if (!(role.equals("ADMIN"))) {
            return "redirect:/forbidden?not_authorized_to_view_admin_view";
        }
        model.addAttribute("role", role);
        return "admin_panel";
    }

    @PostMapping("/admin_panel/search_user")
    // @PreAuthorize("hasRole('ADMIN')") // Only admins can perform this action
    public String searchUser(@RequestParam("username") String username, RedirectAttributes redirectAttributes) {
        User user = userRepository.findByUsername(username);

        if (user == null) {
            // If the user doesn't exist, add an error message to be displayed on the admin view
            redirectAttributes.addFlashAttribute("error", "User not found");
            return "redirect:/admin_panel";
        }

        // If the user exists, redirect to the data dashboard for that user
        return "redirect:/data_dashboard/" + user.getUsername();
    }

    // public String eventView(@PathVariable("eventId") int eventId, Model model, Principal principal)
    @GetMapping("/data_dashboard/{username}")
    // @PreAuthorize("hasRole('ADMIN')") // Only admin can view specific user data
    public String dataDashboardByUsername(@PathVariable("username") String username, Model model, Principal principal) {
        // Using orElseThrow to handle cases where the user does not exist
        User user = userRepository.findByUsername(username);
        // .orElseThrow(() -> 
        //     new ResourceNotFoundException("User with username " + username + " does not exist")
        // );
        User currentUser = userRepository.findByUsername(principal.getName());
        String currentUsername = currentUser.getUsername();
        model.addAttribute("currentUsername", currentUsername);
        model.addAttribute("role", "ADMIN");

        // User user = userOptional.get();

        model.addAttribute("username", user.getUsername());

        // Rol actual del usuario
        model.addAttribute("userRole", user.getRole());

        // Email del usuario
        model.addAttribute("email", user.getEmail());

        // Número de recomendaciones que ha recibido el usuario
        int recommendationToUserCount = recommendationRepository.countRecommendationsFromFriends(user);
        model.addAttribute("recommendationToUserCount", recommendationToUserCount);

        // Número de recomendaciones que ha hecho el usuario
        int recommendationFromUserCount = recommendationRepository.countRecommendationsToFriends(user);
        model.addAttribute("recommendationFromUserCount", recommendationFromUserCount);

        // Numero de bloqueos realizados por el usuario
        int blockedUserCount = blockRepository.countBlockedUsers(user);
        model.addAttribute("blockedUserCount", blockedUserCount);

        // Numero de bloqueos recibidos por el usuario
        int usersBlockingUserCount = blockRepository.countUsersBlockingUser(user);
        model.addAttribute("usersBlockingUserCount", usersBlockingUserCount);

        // Numero de eventos favoritos del usuario
        int favoriteEventCount = user.getFavoriteEvents().size();
        model.addAttribute("favoriteEventCount", favoriteEventCount);

        // Numero de eventos creados por el usuario
        int eventCount = eventRepository.countEventsCreatedByUser(user);
        model.addAttribute("eventCount", eventCount);
        if (eventCount > 0)
            model.addAttribute("createdEvent", true);

    return "data_dashboard";
    }

    @PostMapping("/data_dashboard")
    public String dataDashboardByUsername(@RequestParam("username") String username, 
                                            @RequestParam("newRole") String newRole, Model model) {
        User user = userRepository.findByUsername(username);

        if (user == null) {
            return "error?user_requested_does_not_exist"; // Redirect or return error page
        }
        
        // Change user role according to form data
        user.setRole(newRole);
        userRepository.save(user);

        return "redirect:/data_dashboard/" + user.getUsername();
    }

    // FAVORITES ---------------------------------------------------------------------------------------------------------------------------------------

    @PostMapping("/add_to_favorites")
    public String addToFavorites(@RequestParam("eventId") int eventId,
                                @RequestParam(name = "returnUrl", required = false) String returnUrl,
                                RedirectAttributes redirectAttributes, Principal principal) {
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
            return "redirect:/?event_not_found";
        }

        // If the event exists, add it to the user's favorite events
        Event event = optionalEvent.get();
        List<Event> favoriteEvents = currentUser.getFavoriteEvents();
        favoriteEvents.add(event);
        currentUser.setFavoriteEvents(favoriteEvents);
        userRepository.save(currentUser);

        // Creating a new URL without keeping previous query parameters
        String redirectUrl = (returnUrl != null && !returnUrl.isEmpty()) ? returnUrl : "/event/" + eventId;

        String queryParam = "?__event=" + eventId + "__added_to_favorites";

        // Construct the clean URL
        // Spliting at first ? to ensure previous query parameters are removed
        String finalRedirect = "redirect:" + redirectUrl.split("\\?")[0] + queryParam;

        return finalRedirect;
    }

    @PostMapping("/remove_from_favorites")
    public String removeFromFavorites(@RequestParam("eventId") int eventId,
                                    @RequestParam(name = "returnUrl", required = false) String returnUrl,
                                    Principal principal) {
        // Check login status
        if (principal == null) {
            return "redirect:/forbidden?not_logged_in";
        }

        // If logged in, retrieve the current user
        String username = principal.getName();
        User currentUser = userRepository.findByUsername(username);

        // Retrieve the event by its ID
        Optional<Event> optionalEvent = eventRepository.findById(eventId);

        // Handle the case where the event does not exist
        if (optionalEvent.isEmpty()) {
            return "redirect:/?event_not_found";
        }

        // If the event exists, remove it from the user's favorite events
        Event event = optionalEvent.get();
        List<Event> favoriteEvents = currentUser.getFavoriteEvents();
        
        // Check if the event is in the user's list of favorites
        if (favoriteEvents.contains(event)) {
            favoriteEvents.remove(event);
            currentUser.setFavoriteEvents(favoriteEvents);
            userRepository.save(currentUser);
        }

        // Creating a new URL without keeping previous query parameters
        String redirectUrl = (returnUrl != null && !returnUrl.isEmpty()) ? returnUrl : "/event/" + eventId;

        String queryParam = "?__event=" + eventId + "__removed_from_favorites";

        // Construct the clean URL
        // Spliting at first ? to ensure previous query parameters are removed
        String finalRedirect = "redirect:" + redirectUrl.split("\\?")[0] + queryParam;

        return finalRedirect;
    }




    // BLOCK USERS --------------------------------------------------------------------------------------------------------------------------------------

    // th:action="@{/block_user(username=${event.creator.username})}" 
    @PostMapping("/block_user")
    public String blockUser(@RequestParam("username") String username,
                        @RequestParam("returnBlockUrl") String returnBlockUrl,
                        Principal principal) {
        // Check login status
        if (principal == null) {
            return "redirect:/forbidden?not_logged_in";
        }

        // If logged in, retrieve the current user
        String currentUsername = principal.getName();
        User currentUser = userRepository.findByUsername(currentUsername);

        // Retrieve the user to block by their username
        User userToBlock = userRepository.findByUsername(username);

        // Check if the user to block exists
        if (userToBlock == null) {
            return "redirect:/error?user_to_block_not_found";
        }

        // Check if the user is trying to block themselves
        if (currentUser.equals(userToBlock)) {
            return "redirect:/error?cannot_block_yourself";
        }

        // Check if the user is already blocked
        if (blockRepository.existsByBlockerAndBlocked(currentUser, userToBlock)) {
            return "redirect:/error?user_already_blocked";
        }

        // Block the user
        // TODO: use BlockService
        // TODO: do BlockServiceImpl etc
        // blockService.block(currentUser, userToBlock);
        Block newBlock = new Block();
        newBlock.setBlocker(currentUser);
        newBlock.setBlocked(userToBlock);
        newBlock.setBlockedAt(new Date());

        // Save the block to the database
        blockRepository.save(newBlock);

        // Creating a new URL without keeping previous query parameters
        String redirectUrl = (returnBlockUrl != null && !returnBlockUrl.isEmpty()) ? returnBlockUrl : "/";

        String queryParam = "?__user_blocked_succesfully";

        // Construct the clean URL
        // Spliting at first ? to ensure previous query parameters are removed
        // Variable (condition) ? value_if_true : value_if_false
        String finalRedirect = "redirect:" + redirectUrl.split("\\?")[0] + queryParam;

        return finalRedirect;
        // return "redirect:/?__user_blocked_succesfully";
    }

    // TODO: implement later
    @PostMapping("/unblock_user")
    @Transactional
    public String unblockUser(@RequestParam("usernameToUnblock") String username,
                        @RequestParam("returnBlockUrl") String returnBlockUrl,
                        Principal principal) {
        // Check login status
        if (principal == null) {
            return "redirect:/forbidden?not_logged_in";
        }

        // If logged in, retrieve the current user
        String currentUsername = principal.getName();
        User currentUser = userRepository.findByUsername(currentUsername);

        // Retrieve the user to unblock by their username
        User userToUnblock = userRepository.findByUsername(username);

        // Check if the user to unblock exists
        if (userToUnblock == null) {
            return "redirect:/error?user_to_unblock_not_found";
        }

        // Check if the user is trying to unblock themselves
        if (currentUser.equals(userToUnblock)) {
            return "redirect:/error?cannot_unblock_yourself";
        }

        // Check if the user is not blocked
        if (!blockRepository.existsByBlockerAndBlocked(currentUser, userToUnblock)) {
            return "redirect:/error?user_not_blocked";
        }


        blockRepository.deleteByBlockerAndBlocked(currentUser, userToUnblock);

        // Creating a new URL without keeping previous query parameters
        // Variable (condition) ? value_if_true : value_if_false
        String redirectUrl = (returnBlockUrl != null && !returnBlockUrl.isEmpty()) ? returnBlockUrl : "/user";

        String queryParam = "?__user_unblocked_succesfully";

        // Construct the clean URL
        // Spliting at first ? to ensure previous query parameters are removed
        String finalRedirect = "redirect:" + redirectUrl.split("\\?")[0] + queryParam;

        return finalRedirect;
    }

}
