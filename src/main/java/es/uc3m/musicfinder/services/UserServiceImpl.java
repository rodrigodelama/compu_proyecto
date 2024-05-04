package es.uc3m.musicfinder.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import es.uc3m.musicfinder.model.User;
import es.uc3m.musicfinder.model.UserRepository;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Register method used to save a users password to the database after encrypting it
    @Override
    public void register(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }


    // Follow and unfollow users:

    /*
     * devuelve true si el usuario seguido está contenido en la lista de usuarios que sigue el usuario seguidor.
     * false en caso contrario.
     */
    @Override
    public boolean follows(User follower, User followed) { // para ver si un usuario sigue a otro.
        /*
         * devuelve true si el usuario seguido está contenido en la lista de usuarios 
         * que sigue el usuario seguidor
         *-----------------------------------
         * false en caso contrario. 
         */
        if(follower.getFollowing().contains(followed)) {
            return true;
        } else {
            return false;
        }
    }
    @Override
    public void follow(User follower, User followed) throws UserServiceException {
        //follower: el que quiere ser seguidor
        //followed: el que quiere ser seguido
        //verifica primero que el usuario seguidor no esté intentando seguirse a sí mismo
        //verifica que el usuario seguidor no esté siguiendo ya al usuario seguido
        if((!follows(follower, followed)) && (!follower.equals(followed))) {
            follower.getFollowing().add(followed);
            userRepository.save(follower);
        } else {
            throw new UserServiceException("El usuario no se puede seguir a sí mismo");
        }
    }
    @Override
    public void unfollow(User follower, User followed) throws UserServiceException {
        //verifica que el usuario seguidor esté siguiendo al usuario seguido.
        if(!follows(follower, followed)) {
            throw new UserServiceException("El seguidor no está siguiendo al usuario seguido");
        } else {
            follower.getFollowing().remove(followed);
            userRepository.save(follower);
        }
    }


    // Marcar y desmarcar eventos como favoritos:

    @Override
    public boolean favoritedEvent(User user, Integer eventId) {
        return user.getFavoriteEvents().contains(eventId);
    }
    @Override
    public void favoriteEvent(User user, Integer eventId) throws UserServiceException {
        if (favoritedEvent(user, eventId)) {
            throw new UserServiceException("El evento ya está en favoritos");
        }
        user.getFavoritedEvents().add(eventId);
        userRepository.save(user);
    }
    @Override
    public void unfavoriteEvent(User user, Integer eventId) throws UserServiceException {
        if (!favoritedEvent(user, eventId)) {
            throw new UserServiceException("El evento no está en favoritos");
        }
        user.getFavoriteEvents().remove(eventId);
        userRepository.save(user);
    }


    // Recomendar eventos a usuarios:

    @Override
    public boolean recommended(User user, Event event) {
        return user.getRecommendedEvents().contains(event);
    }
    @Override
    public void recommend(User user, Event event) throws UserServiceException {
        if (recommended(user, event)) {
            throw new UserServiceException("El evento ya fue recomendado");
        }
        user.getRecommendedEvents().add(event);
        userRepository.save(user);
    }


    // Bloquear y desbloquear usuarios:

    @Override
    public boolean blocked(User user, User blocked) {
        return user.getBlockedUsers().contains(blocked);
    }
    @Override
    public void block(User user, User blocked) throws UserServiceException {
        if (blocked(user, blocked)) {
            throw new UserServiceException("El usuario ya está bloqueado");
        }
        user.getBlockedUsers().add(blocked);
        userRepository.save(user);
    }
    @Override
    public void unblock(User user, User blocked) throws UserServiceException {
        if (!blocked(user, blocked)) {
            throw new UserServiceException("El usuario no está bloqueado");
        }
        user.getBlockedUsers().remove(blocked);
        userRepository.save(user);
    }

}
