package es.uc3m.musicfinder.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.uc3m.musicfinder.model.*;
import java.util.List;

@Service
public class RecommendationService {

    @Autowired
    private RecommendationRepository recommendationRepository;

    public int getRecommendationsCountFromFriends(User user) {
        Integer count = recommendationRepository.countRecommendationsFromFriendsExcludingBlokedUsers(user);
        return (count == null) ? 0 : count;
    }

    public int getRecommendationsCountToFriends(User user) {
        Integer count = recommendationRepository.countRecommendationsToFriendsExcludingBlokedUsers(user);
        return (count == null) ? 0 : count;
    }

    // public List<Recommendation> findRecommendationsExcludingBlockedUsers(User user) {
    //     return recommendationRepository.findRecommendationsExcludingBlockedUsers(user);
    // }





}
