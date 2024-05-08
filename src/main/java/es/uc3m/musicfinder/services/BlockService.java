package es.uc3m.musicfinder.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.uc3m.musicfinder.model.*;

@Service
public class BlockService {

    @Autowired
    private BlockRepository BlockRepository;

    public int countBlockedUsers(User user) {
        Integer count = BlockRepository.countBlockedUsers(user);
        return (count == null) ? 0 : count;
    }

    public int countUsersBlockingUser(User user) {
        Integer count = BlockRepository.countUsersBlockingUser(user);
        return (count == null) ? 0 : count;
    }

}
