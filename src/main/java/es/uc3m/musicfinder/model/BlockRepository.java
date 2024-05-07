package es.uc3m.musicfinder.model;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

public interface BlockRepository extends CrudRepository<Block, Integer> {

    // Checks if a specific block exists between a blocker and a blocked user
    boolean existsByBlockerAndBlocked(User blocker, User blocked);

    // Retrieves all users blocked by a given user
    List<Block> findByBlocker(User blocker);

}
