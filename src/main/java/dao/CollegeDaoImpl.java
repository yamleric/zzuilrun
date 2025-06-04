package dao;

import util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CollegeDaoImpl implements CollegeDao {

    @Override
    public List<String> getAllColleges() {
        List<String> colleges = new ArrayList<>();
        String sql = "SELECT college_name FROM colleges WHERE status = 1";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                colleges.add(rs.getString("college_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return colleges;
    }

    @Override
    public int getCollegeIdByName(String collegeName) {
        String sql = "SELECT college_id FROM colleges WHERE college_name = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, collegeName);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("college_id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // 未找到
    }
    @Override
    public boolean addCollege(String collegeName) {
        String sql = "INSERT INTO colleges (college_name, description) VALUES (?, '新添加院系')";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, collegeName);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            if (e.getSQLState().equals("23000")) {
                // 唯一约束违反（重复院系名）
                return false;
            }
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean updateCollege(String oldName, String newName) {
        String sql = "UPDATE colleges SET college_name = ? WHERE college_name = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newName);
            stmt.setString(2, oldName);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            if (e.getSQLState().equals("23000")) {
                // 唯一约束违反（院系名已存在）
                return false;
            }
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean deleteCollege(String collegeName) {
        // 检查是否有用户关联到此院系
        if (hasAssociatedUsers(collegeName)) {
            return false;
        }

        // 软删除：设置状态为0（停用）
        String sql = "UPDATE colleges SET status = 0 WHERE college_name = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, collegeName);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean hasAssociatedUsers(String collegeName) {
        String sql = "SELECT COUNT(*) FROM users u " +
                "JOIN colleges c ON u.college_id = c.college_id " +
                "WHERE c.college_name = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, collegeName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}