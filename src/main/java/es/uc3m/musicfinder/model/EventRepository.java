package es.uc3m.musicfinder.model;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EventRepository extends CrudRepository<Event, Integer> {
    // List<Event> findFirst10ByOrderByTimestampDesc();
    // List<Event> findByUserOrderByTimestampDesc(User user);
    // List<Event> findByResponseToOrderByTimestampAsc(Event event);
    // List<Event> findFirst10ByResponseToIsNullOrderByTimestampDesc();
    // List<Event> findByUserAndResponseToIsNullOrderByTimestampDesc(User user);

    // @Query("SELECT events "
    //     +  "FROM User user "
    //     +  "JOIN user.following followed "
    //     +  "JOIN followed.events events "
    //     +  "WHERE user=?1 AND events.responseTo IS NULL "
    //     +  "ORDER BY events.timestamp DESC")
    // List<Message> eventsFromFollowedUsers(User user, Pageable pageable);
}
