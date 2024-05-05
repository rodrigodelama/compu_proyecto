package es.uc3m.musicfinder.model;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

<<<<<<< HEAD
import java.util.List;

import es.uc3m.musicfinder.model.Event;

=======
>>>>>>> c017b125dacfcf5a31e52555c9b28e2c969faafb
public interface EventRepository extends CrudRepository<Event, Integer> {

    List<Event> findAll();
    // future method for pagination
    // Page<Event> findAll(Pageable pageable);


    // List<Event> findAllEventsOrderByTimestampDesc();
    // List<Event> findAllEventsByUserOrderByTimestampDesc(User user);
    // List<Event> findAllEventsRecommendedByUserOrderByTimestampDesc(User user);
    // List<Event> findAllEventsRecommendedToUserOrderByTimestampDesc(User user);

    // List<Event> eventRecomendationsFromFollowedUsers(User user, Pageable pageable);

}
