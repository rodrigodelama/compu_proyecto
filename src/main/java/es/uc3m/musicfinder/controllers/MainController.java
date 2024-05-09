package es.uc3m.musicfinder.controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.beans.BeanProperty;
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

// Redirect
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// PreAuthorize
// import org.springframework.security.access.prepost.PreAuthorize;


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

            // TODO: CHANGE TO USE CUSTOM QUERY Check if the user has created any events regardless of current role
            List<Event> events = eventRepository.findAllByOrderByTimestampDesc();
            for (Event event : events) {
                if (event.getCreator().equals(currentUser)) {
                    model.addAttribute("createdEvent", true);
                }
                // Check if the current user has this event in favorites (if logged in)
                boolean isFavorite = currentUser.getFavoriteEvents().contains(event);
                model.addAttribute("isFavorite"+event.getId(), isFavorite); // Boolean to indicate if the event is favorited
            }

            if (currentUser != null) {
                // ONLY LOAD RECENT RECOMMENDATIONS (10 most recent for example)
                // Recomendations always exclude blocked users
                List<Recommendation> recentRecommendations = recommendationRepository.findTopNRecommendationsExcludingBlockedUsers(currentUser, PageRequest.of(0, 10));
                
                if (recentRecommendations.isEmpty()) 
                    model.addAttribute("noRecommendations", true);
            }
            model.addAttribute("noRecommendations", true);
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
    // TODO @{/customLogout} see event and home in the navbar for the potential implementation
    @PostMapping(path = "/logout")
    public String logout(@RequestParam(name = "returnLogOutUrl", required = false) String returnUrl) {
        // Creating a new URL without keeping previous query parameters
        String redirectUrl = (returnUrl != null && !returnUrl.isEmpty()) ? returnUrl : "/";

        String queryParam = "?_logged_out";

        // Construct the clean URL
        // Spliting at first ? to ensure previous query parameters are removed
        String finalRedirect = "redirect:" + redirectUrl.split("\\?")[0] + queryParam;

        return finalRedirect;
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

    @GetMapping(path = "/event/{eventId}")
    public String eventView(@PathVariable("eventId") int eventId, Model model, Principal principal) {
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
            return "/404?event_not_found"; // This could be a custom error page or a redirect
        }

        // If the event exists, add it to the model
        Event event = optionalEvent.get();
        model.addAttribute("event", event);

        // Check if the current user has this event in favorites (if logged in)
        if (principal != null) {
            User currentUser = userRepository.findByUsername(principal.getName());
            boolean isFavorite = currentUser.getFavoriteEvents().contains(event);
            model.addAttribute("isFavorite", isFavorite); // Boolean to indicate if the event is favorited
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

        // Load favorite events
        List<Event> favoriteEvents = currentUser.getFavoriteEvents();
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
        if (!(role.equals("ORGANIZER") || role.equals("ADMIN"))) {
            return "redirect:/forbidden?not_authorized";
        }
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
        if (!(role.equals("ORGANIZER") || role.equals("ADMIN"))) {
            return "redirect:/forbidden?not_authorized_to_create_events";
        }

        event.setTimestamp(new Date()); // Set the current timestamp
        event.setCreator(user); // Set the creator to the current user

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


    @GetMapping(path = "/admin_panel")
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
        return "redirect:/data_dashboard/" + user.getId();
    }

    // public String eventView(@PathVariable("eventId") int eventId, Model model, Principal principal)
    @GetMapping("/data_dashboard/{userId}")
    // @PreAuthorize("hasRole('ADMIN')") // Only admin can view specific user data
    public String dataDashboardByUserId(@PathVariable("userId") int userId, Model model) {
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isEmpty()) {
            return "error?user_requested_does_not_exist"; // Redirect or return error page
        }
        model.addAttribute("role", "ADMIN");

        User user = userOptional.get();

        model.addAttribute("username", user.getUsername());

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

        // Numero de eventos creados por el usuario
        int eventCount = eventRepository.countEventsCreatedByUser(user);
        model.addAttribute("eventCount", eventCount);

        // Numero de eventos favoritos del usuario
        int favoriteEventCount = user.getFavoriteEvents().size();
        model.addAttribute("favoriteEventCount", favoriteEventCount);

    return "data_dashboard";
    }

    // FAVORITES ----------------------------------------------------------------------------

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
                                      @RequestParam(name = "returnUrl", required = false) String returnUrl, Principal principal) {
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

    // th:action="@{/block_user(username=${event.creator.username})}" 
    @PostMapping("/block_user")
    public String blockUser(@RequestParam("username") String username,
                        @RequestParam("returnBlockUrl") String returnBlockUrl,
                        RedirectAttributes redirectAttributes, Principal principal) {
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
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to block user.");
            return "redirect:/error?cannot_block_yourself";
        }

        // Check if the user is already blocked
        if (blockRepository.existsByBlockerAndBlocked(currentUser, userToBlock)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to block user.");
            return "redirect:/error?user_already_blocked";
        }

        // Block the user
        Block newBlock = new Block();
        newBlock.setBlocker(currentUser);
        newBlock.setBlocked(userToBlock);
        newBlock.setBlockedAt(new Date());

        // Save the block to the database
        blockRepository.save(newBlock);

        // redirectAttributes.addFlashAttribute("successMessage", "User has been blocked.");

        return "redirect:/?__user_blocked_succesfully";
    }





    // @PostMapping("/block_user")
    // public String blockUser(
    //     @RequestParam("username") String username,
    //     @RequestParam("returnBlockUrl") String returnBlockUrl,
    //     RedirectAttributes redirectAttributes
    // ) {
    //     // Implement your logic to block the user with the given username
    //     boolean isBlocked = blockUserByUsername(username);

    //     if (isBlocked) {
    //         // Add success message to redirect attributes
    //         redirectAttributes.addFlashAttribute("successMessage", "User has been blocked.");
    //     } else {
    //         // Add failure message to redirect attributes
    //         redirectAttributes.addFlashAttribute("errorMessage", "Failed to block user.");
    //     }

    //     // Redirect to the provided URL
    //     return "redirect:" + returnBlockUrl;
    // }

    // private boolean blockUserByUsername(String username) {
    //     // Your logic to block a user by username
    //     // Return true if successful, false otherwise
    //     return true; // Example implementation
    // }
}
