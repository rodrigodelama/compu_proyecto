package es.uc3m.musicfinder.model;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EventRepository extends CrudRepository<Event, Integer> {

    // Return number of events (rows)
    long count();

    // TODO: maybe remove
    List<Event> findAll();

    List<Event> findAllByOrderByTimestampDesc(); // (from newest to oldest)
    // Page<Event> findAllByOrderByTimestampDesc(Pageable pageable); // (from newest to oldest)

    // Find events created by a user
    List<Event> findByCreator(@Param("user") User creator);
    // findByCreatorOrderByTimestampDesc
    List<Event> findByCreatorOrderByTimestampDesc(@Param("user") User creator);

    //eventos creados pore l usuario 
    @Query("SELECT COUNT(e) FROM Event e WHERE e.creator = :user")
    int countEventsCreatedByUser(@Param("user") User user);

    // Remove event by id
    // had to include @Transactional in PostMapping for it to work
    void deleteById(int eventId);

}
