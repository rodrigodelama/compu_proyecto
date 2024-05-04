package es.uc3m.musicfinder.model;

import org.springframework.data.repository.CrudRepository;

import es.uc3m.musicfinder.model.User;

public interface UserRepository extends CrudRepository<User, Integer> {
    User findByEmail(String email);
    User findByUsername(String username); // find by username
}
