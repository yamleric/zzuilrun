package dao;

import model.Event;
import java.util.List;

public interface EventDao {
    List<Event> findAllEvents();
    int insertEvent(Event event);
    boolean updateEvent(Event event);
    boolean deleteEvent(int eventId);
    // 新增查询所有可报名活动方法
    List<Event> getAvailableEvents();
    // 新增按ID查询方法
    Event getEventById(int eventId);
}