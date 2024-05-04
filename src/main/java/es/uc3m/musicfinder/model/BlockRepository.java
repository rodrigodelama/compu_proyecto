package es.uc3m.musicfinder.model;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

public interface BlockRepository extends CrudRepository<Event, Integer> {

    List<User> findAllBlockedUsersByUser(User user);

}
