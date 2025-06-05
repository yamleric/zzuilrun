// controller/StudentController.java
package controller;

import model.Enrollment;
import model.Event;
import model.User;
import service.EnrollmentService;
import view.EnrollmentView;
import view.MyEnrollmentsView;

import java.util.List;

public class StudentController {
    private final User currentUser;
    private final EnrollmentService enrollmentService;

    public StudentController(User user) {
        this.currentUser = user;
        this.enrollmentService = new EnrollmentService();
    }

    public void showEnrollmentView() {
        new EnrollmentView(this, currentUser).setVisible(true);
    }

    public void showMyEnrollments() {
        new MyEnrollmentsView(this, currentUser).setVisible(true);
    }

    public List<Event> getAvailableEvents() {
        return enrollmentService.getAvailableEvents();
    }

    public boolean enrollToEvent(int eventId) {
        return enrollmentService.enrollUserToEvent(currentUser, eventId);
    }

    public List<Enrollment> getUserEnrollments() {
        return enrollmentService.getUserEnrollments(currentUser.getUserId());
    }

    public boolean cancelEnrollment(int enrollmentId) {
        return enrollmentService.cancelEnrollment(enrollmentId);
    }

    public int getEnrollmentsCount() {
        return enrollmentService.getEnrollmentsCount(currentUser.getUserId());
    }
}