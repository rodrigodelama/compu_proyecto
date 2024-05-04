package es.uc3m.musicfinder.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import es.uc3m.musicfinder.model.User;
import es.uc3m.musicfinder.model.UserRepository;
import es.uc3m.musicfinder.model.Event;
import es.uc3m.musicfinder.model.EventRepository;
import es.uc3m.musicfinder.model.Recommendation;
import es.uc3m.musicfinder.model.RecommendationRepository;
import es.uc3m.musicfinder.model.Block;
import es.uc3m.musicfinder.model.BlockRepository;

@Service
public class UserServiceImpl implements UserService {

    // Spring Security PasswordEncoder
    @Autowired
    private PasswordEncoder passwordEncoder;

    // Register method used to save a users password to the database after encrypting it
    @Override
    public void register(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }


    // Backend repositories
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RecommendationRepository recommendationRepository;

    @Autowired
    private BlockRepository blockRepository;

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
    public boolean favoritedEvent(User user, Event event) {
        return user.getFavoriteEvents().contains(event);
    }
    @Override
    public void favoriteEvent(User user, Event event) throws UserServiceException {
        if (favoritedEvent(user, event)) {
            throw new UserServiceException("El evento ya está en favoritos");
        }
        user.getFavoriteEvents().add(event);
        userRepository.save(user);
    }
    @Override
    public void unfavoriteEvent(User user, Event event) throws UserServiceException {
        if (!favoritedEvent(user, event)) {
            throw new UserServiceException("El evento no está en favoritos");
        }
        user.getFavoriteEvents().remove(event);
        userRepository.save(user);
    }


    // Recomendar eventos a usuarios:

    @Override
    public void recommend(User recommender, User recommendTo, Event event) throws UserServiceException {
        if (recommender.equals(recommendTo)) {
            throw new UserServiceException("Cannot recommend an event to oneself.");
        }

        if (recommendationRepository.existsByRecommenderAndRecommendToAndEvent(recommender, recommendTo, event)) {
            throw new UserServiceException("Recommendation already exists.");
        }
        Recommendation recommendation = new Recommendation(recommender, recommendTo, event);
        recommendationRepository.save(recommendation); // Save the new recommendation
    }
    @Override
    public List<Recommendation> getRecommendationsForUser(User recommendTo) {
        return recommendationRepository.findByRecommendTo(recommendTo); // Get all recommendations for a user
    }


    // Bloquear y desbloquear usuarios:

    @Override
    public boolean blocked(User blocker, User blocked) {
        return blockRepository.existsByBlockerAndBlocked(blocker, blocked); // Check if a block exists
    }
    @Override
    public void block(User blocker, User blocked) throws UserServiceException {
        if (blocker.equals(blocked)) {
            throw new UserServiceException("Cannot block oneself.");
        }

        if (blocked(blocker, blocked)) {
            throw new UserServiceException("This user is already blocked.");
        }

        Block block = new Block(blocker, blocked);
        blockRepository.save(block); // Save the new block
    }
    @Override
    public void unblock(User blocker, User blocked) throws UserServiceException {
        if (!blocked(blocker, blocked)) {
            throw new UserServiceException("This user is not blocked.");
        }

        // Find the block and remove it
        List<Block> blocks = blockRepository.findByBlocker(blocker);
        for (Block block : blocks) {
            if (block.getBlocked().equals(blocked)) {
                blockRepository.delete(block);
                break;
            }
        }
    }

}
