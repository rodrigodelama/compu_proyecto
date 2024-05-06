package es.uc3m.musicfinder.model;

import org.springframework.data.repository.CrudRepository;

import es.uc3m.musicfinder.model.Recommendation;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RecommendationRepository extends CrudRepository<Recommendation, Integer> {

    // List<Event> findAllEventsByUserOrderByTimestampDesc(User user);
    // List<Recommendation> findAllEventsRecommendedByUserOrderByTimestampDesc(User user);
    // List<Recommendation> findAllEventsRecommendedToUserOrderByTimestampDesc(User user);
    // List<Event> eventRecomendationsFromFollowedUsers(User user, Pageable pageable);

    boolean existsByRecommenderAndRecommendToAndEvent(User recommender, User recommendTo, Event event);
    List<Recommendation> findByRecommendTo(User recommendTo); // To get all recommendations for a user
    List<Recommendation> findByRecommender(User recommender); // To get all recommendations made by a user

}
