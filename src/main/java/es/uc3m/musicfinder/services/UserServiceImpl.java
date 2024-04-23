package es.uc3m.musicfinder.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import es.uc3m.musicfinder.model.User;
import es.uc3m.musicfinder.model.UserRepository;

@Service
public class UserServiceImpl implements UserService {

    // Autowired is used to inject the UserRepository bean
    @Autowired
    // UserRepository is an interface that extends CrudRepository
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Register method used to save a users password to the database after encrypting it
    @Override
    public void register(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }
}
