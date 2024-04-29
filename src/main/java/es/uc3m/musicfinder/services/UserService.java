package es.uc3m.musicfinder.services;

import es.uc3m.musicfinder.model.User;

public interface UserService {
    void register(User user);
    boolean follows(User follower, User followed); 
    void follow(User follower, User followed) throws UserServiceException;
    void unfollow(User follower, User followed) throws UserServiceException;

}
