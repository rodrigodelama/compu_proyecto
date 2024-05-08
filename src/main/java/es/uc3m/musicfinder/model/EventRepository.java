package es.uc3m.musicfinder.model;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EventRepository extends CrudRepository<Event, Integer> {

    // Return number of events (rows)
    long count();


    // TODO: maybe remove
    List<Event> findAll();

    List<Event> findAllByOrderByTimestampDesc(); // (from newest to oldest)

    // Find events created by a user
    List<Event> findByCreator(@Param("user") User creator);

    //eventos creados pore l usuario 
    @Query("SELECT COUNT(e) FROM Event e WHERE e.creator = :user")
    int countEventsCreatedByUser(@Param("user") User user);

    //Eventos favoritos
    // @Query("SELECT COUNT(f) FROM FavoriteEvent f WHERE f.user = :user")
    // int countFavoriteEventsForUser(@Param("user") User user);

    // future method for pagination
    // Page<Event> findAll(Pageable pageable);

    // List<Event> findAllEventsByUserOrderByTimestampDesc(User user);
    // List<Event> findAllEventsRecommendedByUserOrderByTimestampDesc(User user);
    // List<Event> findAllEventsRecommendedToUserOrderByTimestampDesc(User user);

    // List<Event> eventRecomendationsFromFollowedUsers(User user, Pageable pageable);

}
