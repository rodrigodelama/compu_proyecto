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
    // get all recommendations for a user ordered by timestamp from newest to oldest
    List<Recommendation> findByRecommendTo(User recommendTo);
    // TODO: Query custom para no enviar las recomendaciones de usuarios bloqueados
//     @Query( "SELECT r " +
//             "FROM Recommendation r " +
//             "WHERE r.recommendTo = ?1 " +
//             "AND r.recommender " + 
//             "NOT IN (SELECT b.blocked FROM Block b WHERE b.blocker = ?1)") // To get all recommendations for a user
//     List<Recommendation> findByRecommendToNotBlockedByTimestampDesc(User recommendTo);
    @Query("SELECT r FROM Recommendation r WHERE r.recommendTo = :user AND r.recommender NOT IN (SELECT b.blocked FROM Block b WHERE b.blocker = :user) ORDER BY r.timestamp DESC")
    List<Recommendation> findRecommendationsExcludingBlockedUsers(@Param("user") User user);

    @Query("SELECT r FROM Recommendation r WHERE r.recommendTo = :user AND r.recommender NOT IN (SELECT b.blocked FROM Block b WHERE b.blocker = :user) ORDER BY r.timestamp DESC")
    List<Recommendation> findTopNRecommendationsExcludingBlockedUsers(@Param("user") User user, Pageable pageable);

    @Query("SELECT COUNT(r) FROM Recommendation r WHERE r.timestamp > :date AND r.recommender NOT IN (SELECT b.blocked FROM Block b WHERE b.blocker = :user) AND r.recommendTo = :user")
    int countNewRecommendationsExcludingBlockedUsers(@Param("date") Date date, @Param("user") User user);

    // Home view - only the N most recent
    // get only the first n recommendations for a user ordered by timestamp from newest to oldest
    List<Recommendation> findTopNByRecommendTo(User recommendTo, Pageable pageable);
//     @Query( "SELECT r " +
//             "FROM Recommendation r " +
//             "WHERE r.recommendTo = ?1 " +
//             "AND r.recommender " + 
//             "NOT IN (SELECT b.blocked FROM Block b WHERE b.blocker = ?1)") // To get all recommendations for a user
//     List<Recommendation> findTopNByRecommendToNotBlockedByTimestampDesc(User recommendTo, Pageable pageable);

    // My Recommendations view
    // get all recommendations made by a user ordered by timestamp from newest to oldest
    List<Recommendation> findByRecommenderOrderByTimestampDesc(@Param("user") User recommender); //-- BREAKS
    // List<Recommendation> findByRecommender(User recommender);

    
    //recomendaciones que le han realizado 
    @Query("SELECT COUNT(r) FROM Recommendation r WHERE r.recommendTo = :user ORDER BY r.timestamp DESC")
    int countRecommendationsFromFriends(@Param("user") User user);

    //num recomendaciones realizadas a amigos
    @Query("SELECT COUNT(r) FROM Recommendation r WHERE r.recommender = :user ORDER BY r.timestamp DESC")
    int countRecommendationsToFriends(@Param("user") User user);


}
