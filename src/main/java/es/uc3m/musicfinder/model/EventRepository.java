package es.uc3m.musicfinder.model;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

public interface EventRepository extends CrudRepository<Event, Integer> {

    // List<Event> findAllEventsOrderByTimestampDesc();
    // List<Event> findAllEventsByUserOrderByTimestampDesc(User user);
    // List<Event> findAllEventsRecommendedByUserOrderByTimestampDesc(User user);
    // List<Event> findAllEventsRecommendedToUserOrderByTimestampDesc(User user);

    // List<Event> eventRecomendationsFromFollowedUsers(User user, Pageable pageable);

}
