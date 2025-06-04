package dao;

import model.Event;
import java.util.List;

public interface EventDao {
    List<Event> findAllEvents();
    int insertEvent(Event event);
    boolean updateEvent(Event event);
    boolean deleteEvent(int eventId);
}