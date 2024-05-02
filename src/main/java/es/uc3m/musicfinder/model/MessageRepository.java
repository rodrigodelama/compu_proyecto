package es.uc3m.musicfinder.model;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MessageRepository extends CrudRepository<Message, Integer> {
    List<Message> findFirst10ByOrderByTimestampDesc();
    List<Message> findByUserOrderByTimestampDesc(User user);
    List<Message> findByResponseToOrderByTimestampAsc(Message message);
    List<Message> findFirst10ByResponseToIsNullOrderByTimestampDesc();
    List<Message> findByUserAndResponseToIsNullOrderByTimestampDesc(User user);

    @Query("SELECT messages "
        +  "FROM User user "
        +  "JOIN user.following followed "
        +  "JOIN followed.messages messages "
        +  "WHERE user=?1 AND messages.responseTo IS NULL "
        +  "ORDER BY messages.timestamp DESC")

    List<Message> messagesFromFollowedUsers(User user, Pageable pageable);
}
