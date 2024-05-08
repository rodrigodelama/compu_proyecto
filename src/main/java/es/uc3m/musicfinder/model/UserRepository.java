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

}
