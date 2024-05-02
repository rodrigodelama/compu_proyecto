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

    // Ej 5 p8: seguir y dejar de seguir a usuarios:
    /*
     * devuelve true si el usuario seguido está contenido en la lista de usuarios que sigue el usuario seguidor.
     * false en caso contrario.
     */
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
    public void follow(User follower, User followed) throws UserServiceException {
        //follower: el que quiere ser seguidor.
        //followed: el que quiere ser seguido.
        //verifica primero que el usuario seguidor no esté intentando seguirse a sí mismo.
        //verifica que el usuario seguidor no esté siguiendo ya al usuario seguido.
        if((!follows(follower, followed)) && (!follower.equals(followed))) {
            follower.getFollowing().add(followed);
            userRepository.save(follower);
        } else {
            throw new UserServiceException("El usuario no se puede seguir a sí mismo");
        }
    }
    public void unfollow(User follower, User followed) throws UserServiceException {
        //verifica que el usuario seguidor esté siguiendo al usuario seguido.
        if(!follows(follower, followed)) {
            throw new UserServiceException("El seguidor no está siguiendo al usuario seguido");
        } else {
            follower.getFollowing().remove(followed);
            userRepository.save(follower);
        }
    }
}
