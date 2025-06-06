package service;

import dao.ApprovalDao;
import dao.ApprovalDaoImpl;
import model.Enrollment;
import java.util.List;

public class ApprovalServiceImpl implements ApprovalService {
    private final ApprovalDao approvalDao = new ApprovalDaoImpl();

    @Override
    public List<Enrollment> getPendingEnrollments() {
        return approvalDao.getPendingEnrollments();
    }

    @Override
    public boolean approveEnrollment(int enrollmentId) {
        return approvalDao.updateEnrollmentStatus(enrollmentId, 1); // 1 = approved
    }

    @Override
    public boolean rejectEnrollment(int enrollmentId) {
        return approvalDao.updateEnrollmentStatus(enrollmentId, 2); // 2 = rejected
    }
}