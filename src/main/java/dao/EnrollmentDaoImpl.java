package dao;

import model.Enrollment;
import util.DatabaseUtil;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EnrollmentDaoImpl implements EnrollmentDao {
    @Override
    public boolean enrollUserToEvent(int userId, int eventId, int status) {
        String sql = "INSERT INTO registrations (user_id, event_id, status) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            System.out.println(userId);
            stmt.setInt(1, userId);
            stmt.setInt(2, eventId);
            stmt.setInt(3, status);

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("报名失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean cancelEnrollment(int enrollmentId) {
        String sql = "DELETE FROM registrations WHERE registration_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, enrollmentId);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("取消报名失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Enrollment> getUserEnrollments(int userId) {
        List<Enrollment> enrollments = new ArrayList<>();
        String sql = "SELECT r.registration_id, r.user_id, r.event_id, r.status, r.registration_time, "
                + "e.event_name "
                + "FROM registrations r "
                + "JOIN events e ON r.event_id = e.event_id "
                + "WHERE r.user_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Enrollment enrollment = new Enrollment();
                enrollment.setEnrollmentId(rs.getInt("registration_id"));
                enrollment.setUserId(rs.getInt("user_id"));
                enrollment.setEventId(rs.getInt("event_id"));
                enrollment.setStatus(rs.getInt("status"));

                // 设置活动名称
                enrollment.setEventName(rs.getString("event_name"));

                // 设置报名时间
                Timestamp enrollTime = rs.getTimestamp("registration_time");
                enrollment.setEnrollTime(enrollTime != null ? enrollTime.toLocalDateTime() : LocalDateTime.now());

                enrollments.add(enrollment);
            }
        } catch (SQLException e) {
            System.err.println("获取用户报名失败: " + e.getMessage());
            e.printStackTrace();
        }
        return enrollments;
    }

    @Override
    public boolean isAlreadyEnrolled(int userId, int eventId) {
        String sql = "SELECT COUNT(*) FROM registrations WHERE user_id = ? AND event_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, eventId);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        } catch (SQLException e) {
            System.err.println("检查是否已报名失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}