package es.uc3m.musicfinder.services;

import java.util.List;

import es.uc3m.musicfinder.model.User;
import es.uc3m.musicfinder.model.Event;
import es.uc3m.musicfinder.model.Recommendation;

public interface UserService {

    void register(User user);

    boolean follows(User follower, User followed); 
    void follow(User follower, User followed) throws UserServiceException;
    void unfollow(User follower, User followed) throws UserServiceException;

    boolean favoritedEvent(User user, Event event);
    void favoriteEvent(User user, Event event) throws UserServiceException;
    void unfavoriteEvent(User user, Event event) throws UserServiceException;

    void recommend(User recommender, User recommendTo, Event event) throws UserServiceException;
    public List<Recommendation> getRecommendationsForUser(User recommendTo);

    boolean blocked(User user, User blocked);
    void block(User user, User blocked) throws UserServiceException;
    void unblock(User user, User blocked) throws UserServiceException;
    
}
