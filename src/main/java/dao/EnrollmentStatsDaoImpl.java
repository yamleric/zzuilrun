// 新建DAO实现：EnrollmentStatsDaoImpl.java
package dao;

import model.EnrollmentStats;
import util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EnrollmentStatsDaoImpl implements EnrollmentStatsDao {
    @Override
    public List<EnrollmentStats> getEventEnrollmentStats() {
        List<EnrollmentStats> statsList = new ArrayList<>();
        String sql = "SELECT " +
                "    e.event_id, " +
                "    e.event_name, " +
                "    COUNT(r.registration_id) AS total_registrations, " +
                "    SUM(CASE WHEN r.status = 1 THEN 1 ELSE 0 END) AS approved_count, " +
                "    SUM(CASE WHEN r.status = 0 THEN 1 ELSE 0 END) AS pending_count, " +
                "    SUM(CASE WHEN r.status = 2 THEN 1 ELSE 0 END) AS rejected_count " +
                "FROM events e " +
                "LEFT JOIN registrations r ON e.event_id = r.event_id " +
                "GROUP BY e.event_id, e.event_name " +
                "ORDER BY e.event_id";

        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                EnrollmentStats stats = new EnrollmentStats();
                stats.setEventName(rs.getString("event_name"));
                stats.setTotalRegistrations(rs.getInt("total_registrations"));
                stats.setApprovedCount(rs.getInt("approved_count"));
                stats.setPendingCount(rs.getInt("pending_count"));
                stats.setRejectedCount(rs.getInt("rejected_count"));
                statsList.add(stats);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return statsList;
    }

    @Override
    public List<EnrollmentStats> getCollegeEnrollmentStats() {
        List<EnrollmentStats> statsList = new ArrayList<>();
        String sql = "SELECT " +
                "    c.college_id, " +
                "    c.college_name, " +
                "    COUNT(r.registration_id) AS total_registrations, " +
                "    SUM(CASE WHEN r.status = 1 THEN 1 ELSE 0 END) AS approved_count, " +
                "    SUM(CASE WHEN r.status = 0 THEN 1 ELSE 0 END) AS pending_count, " +
                "    SUM(CASE WHEN r.status = 2 THEN 1 ELSE 0 END) AS rejected_count " +
                "FROM colleges c " +
                "LEFT JOIN users u ON c.college_id = u.college_id " +
                "LEFT JOIN registrations r ON u.user_id = r.user_id " +
                "GROUP BY c.college_id, c.college_name " +
                "ORDER BY total_registrations DESC";

        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                EnrollmentStats stats = new EnrollmentStats();
                stats.setCollegeId(rs.getInt("college_id"));
                stats.setCollegeName(rs.getString("college_name"));
                stats.setTotalRegistrations(rs.getInt("total_registrations"));
                stats.setApprovedCount(rs.getInt("approved_count"));
                stats.setPendingCount(rs.getInt("pending_count"));
                stats.setRejectedCount(rs.getInt("rejected_count"));
                statsList.add(stats);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return statsList;
    }
}