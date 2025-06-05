// dao/EnrollmentDao.java
package dao;

import model.Enrollment;
import model.Event;
import java.util.List;

public interface EnrollmentDao {
    boolean enrollUserToEvent(int userId, int eventId, int status);
    boolean cancelEnrollment(int enrollmentId);
    List<Enrollment> getUserEnrollments(int userId);
    boolean isAlreadyEnrolled(int userId, int eventId);
}