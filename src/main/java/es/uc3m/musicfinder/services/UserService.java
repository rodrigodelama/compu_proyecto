package es.uc3m.musicfinder.services;

import es.uc3m.musicfinder.model.User;

public interface UserService {

    void register(User user);

    boolean follows(User follower, User followed); 
    void follow(User follower, User followed) throws UserServiceException;
    void unfollow(User follower, User followed) throws UserServiceException;

    boolean favoritedEvent(User user, Integer eventId);
    void favoriteEvent(User user, Integer eventId) throws UserServiceException;
    void unfavoriteEvent(User user, Integer eventId) throws UserServiceException;

    boolean recommended(User user, Event event);
    void recommend(User user, Event event) throws UserServiceException;
    // void unrecommend(User user, Event event) throws UserServiceException; ??

    boolean blocked(User user, User blocked);
    void block(User user, User blocked) throws UserServiceException;
    void unblock(User user, User blocked) throws UserServiceException;
    
}
