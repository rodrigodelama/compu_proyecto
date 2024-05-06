package es.uc3m.musicfinder.model;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

public interface EventRepository extends CrudRepository<Event, Integer> {

    Event findById(int id);

    //
    List<Event> findAll();

    // which one for chronological order?
    List<Event> findAllByOrderByTimestampAsc();
    List<Event> findAllByOrderByTimestampDesc();

    // future method for pagination
    // Page<Event> findAll(Pageable pageable);

    // List<Event> findAllEventsByUserOrderByTimestampDesc(User user);
    // List<Event> findAllEventsRecommendedByUserOrderByTimestampDesc(User user);
    // List<Event> findAllEventsRecommendedToUserOrderByTimestampDesc(User user);

    // List<Event> eventRecomendationsFromFollowedUsers(User user, Pageable pageable);

}
