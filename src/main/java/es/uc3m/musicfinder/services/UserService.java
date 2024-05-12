package es.uc3m.musicfinder.services;

import java.util.List;

import es.uc3m.musicfinder.model.Block;
import es.uc3m.musicfinder.model.Event;
import es.uc3m.musicfinder.model.Recommendation;
import es.uc3m.musicfinder.model.User;

public interface UserService {

    void register(User user);

    // Favorites
    boolean favoritedEvent(User user, Event event);
    void favoriteEvent(User user, Event event) throws UserServiceException;
    void unfavoriteEvent(User user, Event event) throws UserServiceException;

    // Blocks
    boolean blocked(User user, User blocked);
    void block(User user, User blocked) throws UserServiceException;
    void unblock(User user, User blocked) throws UserServiceException;

    // Recommendations
    void recommend(User recommender, User recommendTo, Event event) throws UserServiceException;
    List<Recommendation> getRecommendationsForUser(User recommendTo);

}
