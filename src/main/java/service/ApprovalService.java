package service;

import model.Enrollment;
import java.util.List;

public interface ApprovalService {
    // 获取所有待审核报名
    List<Enrollment> getPendingEnrollments();

    // 批准报名
    boolean approveEnrollment(int enrollmentId);

    // 拒绝报名
    boolean rejectEnrollment(int enrollmentId);
}