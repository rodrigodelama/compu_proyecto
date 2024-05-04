package es.uc3m.musicfinder.model;

import org.springframework.data.repository.CrudRepository;

import es.uc3m.musicfinder.model.Recommendation;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RecomendationRepository extends CrudRepository<Event, Integer> {

    List<Event> findAllEventsByUserOrderByTimestampDesc(User user);
    // List<Recommendation> findAllEventsRecommendedByUserOrderByTimestampDesc(User user);

    List<Recommendation> findAllEventsRecommendedToUserOrderByTimestampDesc(User user);

    List<Event> eventRecomendationsFromFollowedUsers(User user, Pageable pageable);

}
