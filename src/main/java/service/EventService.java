package service;

import dao.EventDao;
import dao.EventDaoImpl;
import model.Event;

import java.util.List;

//public class EventService {
//    private final EventDao eventDao = new EventDaoImpl();
//
//    public List<Event> getAllEvents() {
//        return eventDao.findAllEvents();
//    }
//
//    public boolean addEvent(Event event) {
//        return eventDao.insertEvent(event) > 0;
//    }
//
//    public boolean updateEvent(Event event) {
//        return eventDao.updateEvent(event);
//    }
//
//    public boolean deleteEvent(int eventId) {
//        return eventDao.deleteEvent(eventId);
//    }
//}
public class EventService {
    private final EventDao eventDao = new EventDaoImpl();

    public int insertEvent(Event event) {
        return eventDao.insertEvent(event);
    }

    public boolean updateEvent(Event event) {
        return eventDao.updateEvent(event);
    }

    public List<Event> getAllEvents() {
        return eventDao.findAllEvents();
    }

    public boolean deleteEvent(int eventId) {
        return eventDao.deleteEvent(eventId);
    }
}