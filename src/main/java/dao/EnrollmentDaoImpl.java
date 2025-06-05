// dao/EnrollmentDaoImpl.java
package dao;

import model.Enrollment;
import model.Event;
import util.DatabaseUtil;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EnrollmentDaoImpl implements EnrollmentDao {
    /**
     * 将用户报名参加活动的信息添加到数据库中
     *
     * @param userId 用户ID，表示报名的用户
     * @param eventId 活动ID，表示用户想要报名参加的活动
     * @param status 报名状态，表示用户报名的当前状态
     * @return 如果报名成功，返回true；否则，返回false
     */
    @Override
    public boolean enrollUserToEvent(int userId, int eventId, int status) {
        // SQL语句，用于插入用户报名活动的信息到数据库中
        String sql = "INSERT INTO registrations (user_id, event_id, status) VALUES (?, ?, ?)";

        try (// 获取数据库连接
             Connection conn = DatabaseUtil.getConnection();
             // 准备SQL语句执行对象
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            // 打印用户ID，用于调试目的
            System.out.println(userId);
            // 设置SQL语句中的用户ID参数
            stmt.setInt(1, userId);
            // 设置SQL语句中的活动ID参数
            stmt.setInt(2, eventId);
            // 设置SQL语句中的报名状态参数
            stmt.setInt(3, status);

            // 执行SQL语句并获取受影响的行数
            int affectedRows = stmt.executeUpdate();
            // 如果受影响的行数大于0，表示报名成功
            return affectedRows > 0;
        } catch (SQLException e) {
            // 如果发生SQL异常，打印错误信息并返回false表示报名失败
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

    /**
     * 获取用户的报名记录
     * 通过用户ID查询用户在所有活动中的报名记录，并包含活动的基本信息
     *
     * @param userId 用户ID
     * @return 用户的报名记录列表
     */
    @Override
    public List<Enrollment> getUserEnrollments(int userId) {
        List<Enrollment> enrollments = new ArrayList<>();
        // SQL查询语句，用于获取用户的报名记录及关联的活动信息
        String sql = "SELECT r.registration_id, r.user_id, r.event_id, r.status, r.registration_time, " +
                "e.event_name, e.start_time, e.location " +
                "FROM registrations r " +
                "JOIN events e ON r.event_id = e.event_id " +
                "WHERE r.user_id = ?";

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

                // 处理时间
                Timestamp enrollTime = rs.getTimestamp("registration_time");
                enrollment.setEnrollTime(enrollTime != null ? enrollTime.toLocalDateTime() : LocalDateTime.now());

                // 创建关联的活动对象
                Event event = new Event();
                event.setEventId(enrollment.getEventId());
                event.setEventName(rs.getString("event_name"));

                Timestamp startTime = rs.getTimestamp("start_time");
                if (startTime != null) {
                    event.setStartTime(startTime.toLocalDateTime());
                }

                event.setLocation(rs.getString("location"));
                enrollment.setEvent(event);

                enrollments.add(enrollment);
            }
        } catch (SQLException e) {
            System.err.println("获取用户报名失败: " + e.getMessage());
            e.printStackTrace();
        }
        return enrollments;
    }

    /**
 * 检查用户是否已报名参加特定活动
 *
 * @param userId 用户ID，用于标识用户
 * @param eventId 活动ID，用于标识活动
 * @return 如果用户已报名参加该活动，则返回true；否则返回false
 */
@Override
public boolean isAlreadyEnrolled(int userId, int eventId) {
    // SQL查询语句，用于检查用户是否已报名参加特定活动
    String sql = "SELECT COUNT(*) FROM registrations WHERE user_id = ? AND event_id = ?";

    try (Connection conn = DatabaseUtil.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        // 设置查询参数，包括用户ID和活动ID
        stmt.setInt(1, userId);
        stmt.setInt(2, eventId);

        // 执行查询并获取结果集
        ResultSet rs = stmt.executeQuery();
        // 如果查询结果有数据，判断用户是否已报名
        if (rs.next()) {
            return rs.getInt(1) > 0;
        }
        return false;
    } catch (SQLException e) {
        // 打印异常信息，便于问题追踪
        System.err.println("检查是否已报名失败: " + e.getMessage());
        e.printStackTrace();
        return false;
    }
}

}