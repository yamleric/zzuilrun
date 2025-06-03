package dao;

import util.DatabaseUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CollegeDaoImpl implements CollegeDao {
    // SQL 语句
    private static final String GET_ALL_COLLEGES = "SELECT college_name FROM colleges ORDER BY college_name";
    private static final String ADD_COLLEGE = "INSERT INTO colleges (college_name) VALUES (?)";
    private static final String DELETE_COLLEGE = "DELETE FROM colleges WHERE college_name = ?";
    private static final String GET_COLLEGE_ID = "SELECT id FROM colleges WHERE college_name = ?";

    @Override
    public List<String> getAllCollegeNames() {
        List<String> colleges = new ArrayList<>();

        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(GET_ALL_COLLEGES)) {

            while (rs.next()) {
                colleges.add(rs.getString("college_name"));
            }
        } catch (SQLException e) {
            System.err.println("数据库错误: " + e.getMessage());
        }
        return colleges;
    }

    @Override
    public boolean addCollege(String collegeName) {
        if (collegeName == null || collegeName.trim().isEmpty()) {
            return false;
        }

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(ADD_COLLEGE)) {

            pstmt.setString(1, collegeName.trim());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("添加院系失败: " + collegeName + " - " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean deleteCollegeByName(String collegeName) {
        if (collegeName == null || collegeName.trim().isEmpty()) {
            return false;
        }

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(DELETE_COLLEGE)) {

            pstmt.setString(1, collegeName.trim());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("删除院系失败: " + collegeName + " - " + e.getMessage());
            return false;
        }
    }

    // 如果需要此方法，请保持签名一致
    public int getCollegeIdByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return -1;
        }

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(GET_COLLEGE_ID)) {

            pstmt.setString(1, name.trim());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            System.err.println("获取院系ID失败: " + name + " - " + e.getMessage());
        }
        return -1;
    }
}