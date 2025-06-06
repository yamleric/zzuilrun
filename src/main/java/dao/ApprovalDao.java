package dao;

import model.Enrollment;
import java.util.List;

public interface ApprovalDao {
    // 获取所有待审核的报名
    List<Enrollment> getPendingEnrollments();

    // 更新报名状态
    boolean updateEnrollmentStatus(int enrollmentId, int status);
}