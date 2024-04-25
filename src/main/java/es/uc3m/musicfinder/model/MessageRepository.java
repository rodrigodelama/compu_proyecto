package es.uc3m.musicfinder.model;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MessageRepository extends CrudRepository<Message, Integer> {
    // Ej 4 P7 New method declaration for MessageRepository
    List<Message> findFirst10ByOrderByTimestampDesc();
    //Ej 5 P7:
    List<Message> findByUserOrderByTimestampDesc(User user);
    /*
     * Sin embargo, queremos tener control sobre el orden de los mensajes. Por lo tanto, es mejor declarar 
     * un nuevo método en MessageRepository 
     * que devuelva los mensajes del usuario que le pases como parámetro ordenados de más a menos reciente
    */
    //Ej 2 p8:
    List<Message> findByResponseToOrderByTimestampAsc(Message message);
    //Ej 3 p8: ocultación de mensajes de respuesta
    //en vista principal
    List<Message> findFirst10ByResponseToIsNullOrderByTimestampDesc();
    //en vista de perfil de usuario:
    List<Message> findByUserAndResponseToIsNullOrderByTimestampDesc(User user);
    //en vista de mensaje: aborta con un error 403 si el mensaje a mostrar es un mensaje de respuesta.

    //Ej 8 p8: Mostrar mensajes de los usuarios a quien se sigue en la vista principal
    //List<Message> findFirst10ByUserInOrderByTimestampDesc(List<User> users); 
    /*
     * La cláusula byUserIn hace que la consulta solo seleccione mensajes de los usuarios proporcionados 
     * en la lista que recibe el método.
     */
    // Nueva declaración de método ej 8 p8:
    @Query("SELECT messages "
        +  "FROM User user "
        +  "JOIN user.following followed "
        +  "JOIN followed.messages messages "
        +  "WHERE user=?1 AND messages.responseTo IS NULL "
        +  "ORDER BY messages.timestamp DESC")
    
    List<Message> messagesFromFollowedUsers(User user, Pageable pageable);
}
