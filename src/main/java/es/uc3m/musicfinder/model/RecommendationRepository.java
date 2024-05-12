package es.uc3m.musicfinder.model;

import java.util.List;
import java.util.Date;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RecommendationRepository extends CrudRepository<Recommendation, Integer> {

    // TODO: maybe remove
    boolean existsByRecommenderAndRecommendToAndEvent(User recommender, User recommendTo, Event event);

    // Recommendations view
    List<Recommendation> findByRecommendTo(User recommendTo);

    //Buscar recomendaciones de amigos excluyendo a los usuarios bloqueados
    @Query("SELECT r FROM Recommendation r WHERE r.recommendTo = :user AND r.recommender NOT IN (SELECT b.blocked FROM Block b WHERE b.blocker = :user) ORDER BY r.timestamp DESC")
    List<Recommendation> findRecommendationsExcludingBlockedUsers(@Param("user") User user);


    @Query("SELECT r FROM Recommendation r WHERE r.recommendTo = :user AND r.recommender NOT IN (SELECT b.blocked FROM Block b WHERE b.blocker = :user) ORDER BY r.timestamp DESC")
    List<Recommendation> findTopNRecommendationsExcludingBlockedUsers(@Param("user") User user, Pageable pageable);

    // Home view - only the N most recent
    List<Recommendation> findTopNByRecommendTo(User recommendTo, Pageable pageable);


    // My Recommendations view
    List<Recommendation> findByRecommenderOrderByTimestampDesc(@Param("user") User recommender);


    //USER Page
    //recomendaciones que le han realizado 
    @Query("SELECT COUNT(r) FROM Recommendation r WHERE r.recommendTo = :user AND r.recommender NOT IN (SELECT b.blocked FROM Block b WHERE b.blocker = :user) ORDER BY r.timestamp DESC")
    int countRecommendationsFromFriendsExcludingBlokedUsers(@Param("user") User user);

    //num recomendaciones realizadas a amigos
    @Query("SELECT COUNT(r) FROM Recommendation r WHERE r.recommender = :user AND r.recommender NOT IN (SELECT b.blocked FROM Block b WHERE b.blocker = :user) ORDER BY r.timestamp DESC")
    int countRecommendationsToFriendsExcludingBlokedUsers(@Param("user") User user);


    //ADMIN PANEL
    //recomendaciones que le han realizado 
    @Query("SELECT COUNT(r) FROM Recommendation r WHERE r.recommendTo = :user ORDER BY r.timestamp DESC")
    int countRecommendationsFromFriends(@Param("user") User user);

    //num recomendaciones realizadas a amigos
    @Query("SELECT COUNT(r) FROM Recommendation r WHERE r.recommender = :user ORDER BY r.timestamp DESC")
    int countRecommendationsToFriends(@Param("user") User user);

}
