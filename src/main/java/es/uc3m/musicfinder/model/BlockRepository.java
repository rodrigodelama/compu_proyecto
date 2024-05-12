package es.uc3m.musicfinder.model;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

public interface BlockRepository extends CrudRepository<Block, Integer> {

    // Checks if a specific block exists between a blocker and a blocked user
    boolean existsByBlockerAndBlocked(User blocker, User blocked);

    // Retrieves a specific block between a blocker and a blocked user
    Block findByBlockerAndBlocked(User blocker, User blocked);

    // Delete Block
    // had to include @Transactional in PostMapping for it to work
    void deleteByBlockerAndBlocked(User blocker, User blocked);

    // Retrieves all users blocked by a given user
    // Used to unblock users from the blocked list in the profile page
    List<Block> findByBlocker(User blocker);

    //num usuarios que tiene bloqueado el usuario
    @Query("SELECT COUNT(b) FROM Block b WHERE b.blocker = :user")
    int countBlockedUsers(@Param("user") User user);

    //num usuarios que bloquean a este usuario
    @Query("SELECT COUNT(b) FROM Block b WHERE b.blocked = :user")
    int countUsersBlockingUser(@Param("user") User user);

    // Retrieves all users blocked by a given user
    // Used to only show recommendations from users that are not blocked
    @Query("SELECT b.blocked FROM Block b WHERE b.blocker = :user")
    List<User> findBlockedUsers(@Param("user") User user);

}
