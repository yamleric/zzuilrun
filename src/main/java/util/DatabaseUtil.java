package util;

import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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
    // 初始化超级管理员
    public static void initSuperAdmin() {
        String sql = "INSERT INTO users (username, password, real_name, college_id, role) "
                + "SELECT 'admin', ?, '系统管理员', 1, 2 "
                + "WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'admin')";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, BCrypt.hashpw("admin123", BCrypt.gensalt()));
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("初始化超级管理员失败", e);
        }
    }
}