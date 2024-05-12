package es.uc3m.musicfinder.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.uc3m.musicfinder.model.*;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    public int countEventsCreatedByUser(User user) {
        Integer count = eventRepository.countEventsCreatedByUser(user);
        return (count == null) ? 0 : count;
    }

    // Deprecated - We just count the size of the list of events favorited by the user
    // public int countFavoritedEventsByUser(User user) {
    //     Integer count = eventRepository.countFavoritedEventsByUser(user);
    //     return (count == null) ? 0 : count;
    // }

}
