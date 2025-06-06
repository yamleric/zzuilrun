package dao;

import model.Enrollment;
import util.DatabaseUtil;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ApprovalDaoImpl implements ApprovalDao {
    @Override
    public List<Enrollment> getPendingEnrollments() {
        List<Enrollment> enrollments = new ArrayList<>();
        String sql = "SELECT r.registration_id, u.user_id, u.username, u.real_name, "
                + "e.event_id, e.event_name, r.status, r.registration_time "
                + "FROM registrations r "
                + "JOIN users u ON r.user_id = u.user_id "
                + "JOIN events e ON r.event_id = e.event_id "
                + "WHERE r.status = 0"; // 0 = pending approval

        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Enrollment enrollment = new Enrollment();
                enrollment.setEnrollmentId(rs.getInt("registration_id"));
                enrollment.setUserId(rs.getInt("user_id"));
                enrollment.setUsername(rs.getString("username"));
                enrollment.setRealName(rs.getString("real_name"));
                enrollment.setEventId(rs.getInt("event_id"));
                enrollment.setEventName(rs.getString("event_name"));
                enrollment.setStatus(rs.getInt("status"));

                Timestamp enrollTimestamp = rs.getTimestamp("registration_time");
                if (enrollTimestamp != null) {
                    enrollment.setEnrollTime(enrollTimestamp.toLocalDateTime());
                }

                enrollments.add(enrollment);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return enrollments;
    }

    @Override
    public boolean updateEnrollmentStatus(int enrollmentId, int status) {
        String sql = "UPDATE registrations SET status = ? WHERE registration_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, status);
            stmt.setInt(2, enrollmentId);

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}