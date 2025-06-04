package util;

import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;

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
            System.out.println("超级管理员初始化成功");
            pstmt.setString(1, BCrypt.hashpw("admin123", BCrypt.gensalt()));
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

}