package es.uc3m.musicfinder.model;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import es.uc3m.musicfinder.model.User;

public interface UserRepository extends CrudRepository<User, Integer> {
    User findByUsername(String username);

    // Deprecated method - we count the list of favorites in the User object
    //Eventos favoritos
    // @Query("SELECT COUNT(f) FROM User f WHERE f.favorite = :user")
    // int countFavoritedEventsByUser(@Param("user") User user);

}
