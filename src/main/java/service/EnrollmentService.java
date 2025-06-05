// service/EnrollmentService.java
package service;

import dao.EnrollmentDao;
import dao.EnrollmentDaoImpl;
import dao.EventDao;
import dao.EventDaoImpl;
import model.Enrollment;
import model.Event;
import model.User;

import java.util.List;

public class EnrollmentService {
    private final EnrollmentDao enrollmentDao = new EnrollmentDaoImpl();
    private final EventDao eventDao = new EventDaoImpl();

    public List<Event> getAvailableEvents() {
        return eventDao.getAvailableEvents();
    }

    public boolean enrollUserToEvent(User user, int eventId) {
        // 1. 检查是否已报名
        if (enrollmentDao.isAlreadyEnrolled(user.getUserId(), eventId)) {
            return false;
        }

        // 2. 检查性别限制
        Event event = eventDao.getEventById(eventId);
        if (event != null && !isGenderAllowed(user.getGender(), event.getGenderLimit())) {
            return false;
        }

        // 3. 检查活动状态是否可用
        if (event.getStatus() != 1) { // 1表示开放报名
            return false;
        }

        // 执行报名（默认状态为0-待审核）
        return enrollmentDao.enrollUserToEvent(user.getUserId(), eventId, 0);
    }

    public boolean cancelEnrollment(int enrollmentId) {
        return enrollmentDao.cancelEnrollment(enrollmentId);
    }

    public List<Enrollment> getUserEnrollments(int userId) {
        return enrollmentDao.getUserEnrollments(userId);
    }

    public int getEnrollmentsCount(int userId) {
        return enrollmentDao.getUserEnrollments(userId).size();
    }

    private boolean isGenderAllowed(String userGender, String eventGenderLimit) {
        return "A".equals(eventGenderLimit) ||
                (eventGenderLimit != null && eventGenderLimit.equals(userGender));
    }
}