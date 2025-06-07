package util;

import model.Event;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseUtil {
    private static final String URL = "jdbc:mysql://localhost:3306/sports_meeting_system?characterEncoding=utf8&useSSL=false&serverTimezone=GMT%2B8";
    private static final String USER = "root";
    private static final String PASSWORD = "123456";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("数据库驱动加载失败:");
            e.printStackTrace();
        }
    }

    public static Connection getConnection() {
        try {
            System.out.println("获取数据库连接成功");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            System.err.println("获取数据库连接失败:");
            e.printStackTrace();
            return null;
        }

    }
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("关闭数据库连接时出错:");
                e.printStackTrace();
            }
        }
    }
    public static void initColleges() {
        String sql = "INSERT INTO colleges (college_name, college_code, status) VALUES "
                + "('计算机学院', '001', 1), "
                + "('电气学院', '002', 1), "
                + "('机械学院', '003', 1), "
                + "('软件学院', '004', 1) "
                + "ON DUPLICATE KEY UPDATE college_name = VALUES(college_name), college_code = VALUES(college_code)";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
            System.out.println("院系数据初始化完成");
        } catch (SQLException e) {
            System.err.println("初始化院系数据失败: " + e.getMessage());
        }
    }
    // 初始化超级管理员
    public static void initSuperAdmin() {
        String sql = "INSERT INTO users (username, password, real_name, college_id, role, gender) "
                + "SELECT 'admin', ?, '系统管理员', 1, 2, 'M' "  // 指定性别为'M'
                + "WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'admin')";

        try (Connection conn = getConnection();

             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, BCrypt.hashpw("admin123", BCrypt.gensalt()));
            System.out.println("超级管理员初始化成功");
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("初始化超级管理员失败", e);
        }
    }

    public static String fetchCollegeName(int collegeId) {
        String sql = "SELECT college_name FROM colleges WHERE college_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, collegeId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("college_name");
                }
            }
        } catch (SQLException e) {
            System.err.println("获取学院名称失败 (ID: " + collegeId + "): " + e.getMessage());
        }

        return "未知学院"; // 默认返回值
    }
    // 在DatabaseUtil类中实现
    public static String fetchCollegeCode(String collegeName) {
        String sql = "SELECT college_code FROM colleges WHERE college_name = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, collegeName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("college_code");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "CODE-UNKNOWN";
    }
    // 添加取消报名的方法
    public static boolean cancelEnrollment(int userId, int eventId) {
        String sql = "DELETE FROM registrations WHERE user_id = ? AND event_id = ? AND status = 0"; // 只能取消待审核的报名

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, eventId);

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 新增获取活动信息的方法
    public static Event getEventDetails(int eventId) {
        String sql = "SELECT * FROM events WHERE event_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, eventId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Event event = new Event();
                event.setEventId(rs.getInt("event_id"));
                event.setEventName(rs.getString("event_name"));
                event.setEventType(rs.getInt("event_type"));
                event.setGenderLimit(rs.getString("gender_limit"));
                event.setMinParticipants(rs.getInt("min_participants"));
                event.setMaxParticipants(rs.getInt("max_participants"));
                event.setStartTime(rs.getTimestamp("start_time").toLocalDateTime());
                event.setEndTime(rs.getTimestamp("end_time").toLocalDateTime());
                event.setLocation(rs.getString("location"));
                event.setDescription(rs.getString("description"));
                event.setStatus(rs.getInt("status"));
                return event;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 新增获取可用活动的方法
    public static List<Event> getAvailableEvents() {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT * FROM events WHERE status = 1 AND start_time > NOW()";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                Event event = new Event();
                event.setEventId(rs.getInt("event_id"));
                event.setEventName(rs.getString("event_name"));
                event.setEventType(rs.getInt("event_type"));
                event.setGenderLimit(rs.getString("gender_limit"));
                event.setMinParticipants(rs.getInt("min_participants"));
                event.setMaxParticipants(rs.getInt("max_participants"));

                Timestamp start = rs.getTimestamp("start_time");
                Timestamp end = rs.getTimestamp("end_time");
                Timestamp create = rs.getTimestamp("create_time");

                event.setStartTime(start != null ? start.toLocalDateTime() : null);
                event.setEndTime(end != null ? end.toLocalDateTime() : null);
                event.setCreateTime(create != null ? create.toLocalDateTime() : null);

                event.setLocation(rs.getString("location"));
                event.setDescription(rs.getString("description"));
                event.setStatus(rs.getInt("status"));
                events.add(event);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return events;
    }

    // 新增获取用户报名的方法
    public static List<Event> getUserEnrollments(int userId) {
        System.out.println("DEBUG: 查询用户" + userId + "的报名记录");
        List<Event> events = new ArrayList<>();
        String sql = "SELECT e.event_id, e.event_name, e.start_time, e.location, " +
                "r.status AS enrollment_status " +
                "FROM events e " +
                "JOIN registrations r ON e.event_id = r.event_id " +
                "WHERE r.user_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            int count = 0;
            while (rs.next()) {
                count++;
                Event event = new Event();
                event.setEventId(rs.getInt("event_id"));
                event.setEventName(rs.getString("event_name"));

                Timestamp start = rs.getTimestamp("start_time");
                if (start != null) {
                    event.setStartTime(start.toLocalDateTime());
                }

                event.setLocation(rs.getString("location"));
                // 关键：使用报名表中的状态
                event.setStatus(rs.getInt("enrollment_status"));
                events.add(event);
            }
            System.out.println("DEBUG: 查询到" + count + "条报名记录");
        } catch (SQLException e) {
            System.err.println("ERROR: 查询报名记录失败 - " + e.getMessage());
            e.printStackTrace();
            // 返回空列表避免界面错误
            return new ArrayList<>();
        } catch (Exception e) {
            System.err.println("ERROR: 未知错误 - " + e.getMessage());
            e.printStackTrace();
        }
        return events;
    }

    // 新增报名用户到活动的方法
    public static boolean enrollUserToEvent(int userId, int eventId) {
        String sql = "INSERT INTO registrations (user_id, event_id) VALUES (?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, eventId);

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public static int getCollegeIdByName(String collegeName) {
        String sql = "SELECT college_id FROM colleges WHERE college_name = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, collegeName);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("college_id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0; // 默认值
    }
}