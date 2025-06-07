package dao;

import model.Event;
import util.DatabaseUtil;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EventDaoImpl implements EventDao {
    @Override
    public List<Event> getAvailableEvents() {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT * FROM events WHERE status = ? AND start_time > NOW()";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, Event.STATUS_OPEN);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Event event = extractEventFromResultSet(rs);
                events.add(event);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return events;
    }

    @Override
    public Event getEventById(int eventId) {
        String sql = "SELECT * FROM events WHERE event_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, eventId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return extractEventFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    private Event extractEventFromResultSet(ResultSet rs) throws SQLException {
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
        event.setCreateTime(rs.getTimestamp("create_time").toLocalDateTime());
        return event;
    }
    @Override
    public List<Event> findAllEvents() {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT * FROM events";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                events.add(mapRowToEvent(rs));
            }
        } catch (SQLException e) {
            // 修复：添加日志方法或直接打印异常
            e.printStackTrace();
        }
        return events;
    }

    @Override
    public int insertEvent(Event event) {
        String sql = "INSERT INTO events (" +
                "event_name, event_type, gender_limit, min_participants, " +
                "max_participants, start_time, end_time, location, " +
                "description, status, create_time" +
                ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // 设置参数 - 重构为使用实际值
            stmt.setString(1, event.getEventName());
            stmt.setInt(2, event.getEventType());
            stmt.setString(3, event.getGenderLimit());
            stmt.setInt(4, event.getMinParticipants());
            stmt.setInt(5, event.getMaxParticipants());

            // 修复时间字段处理 - 正确转换和检查null
            stmt.setTimestamp(6, event.getStartTime() != null ?
                    Timestamp.valueOf(event.getStartTime()) : null);
            stmt.setTimestamp(7, event.getEndTime() != null ?
                    Timestamp.valueOf(event.getEndTime()) : null);

            stmt.setString(8, event.getLocation());
            stmt.setString(9, event.getDescription());
            stmt.setInt(10, event.getStatus());

            // 创建时间设置为当前时间
            stmt.setTimestamp(11, new Timestamp(System.currentTimeMillis()));

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("插入事件失败，没有行受影响");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("插入事件失败，未获取到ID");
                }
            }
        } catch (SQLException e) {
            // 修复：添加实际错误日志方法
            System.err.println("插入事件失败: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public boolean updateEvent(Event event) {
        String sql = "UPDATE events SET " +
                "event_name = ?, event_type = ?, gender_limit = ?, " +
                "min_participants = ?, max_participants = ?, " +
                "start_time = ?, end_time = ?, location = ?, " +
                "description = ?, status = ? " +
                "WHERE event_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, event.getEventName());
            stmt.setInt(2, event.getEventType());
            stmt.setString(3, event.getGenderLimit());
            stmt.setInt(4, event.getMinParticipants());
            stmt.setInt(5, event.getMaxParticipants());

            // 修复时间字段处理
            stmt.setTimestamp(6, event.getStartTime() != null ?
                    Timestamp.valueOf(event.getStartTime()) : null);
            stmt.setTimestamp(7, event.getEndTime() != null ?
                    Timestamp.valueOf(event.getEndTime()) : null);

            stmt.setString(8, event.getLocation());
            stmt.setString(9, event.getDescription());
            stmt.setInt(10, event.getStatus());
            stmt.setInt(11, event.getEventId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("更新事件失败: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean deleteEvent(int eventId) {
        String sql = "DELETE FROM events WHERE event_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, eventId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 修复映射方法 - 添加缺失的时间字段处理
    private Event mapRowToEvent(ResultSet rs) throws SQLException {
        Event event = new Event();
        event.setEventId(rs.getInt("event_id"));
        event.setEventName(rs.getString("event_name"));
        event.setEventType(rs.getInt("event_type"));
        event.setGenderLimit(rs.getString("gender_limit"));
        event.setMinParticipants(rs.getInt("min_participants"));
        event.setMaxParticipants(rs.getInt("max_participants"));
        event.setLocation(rs.getString("location"));
        event.setDescription(rs.getString("description"));
        event.setStatus(rs.getInt("status"));

        // 正确转换数据库时间字段
        Timestamp startTime = rs.getTimestamp("start_time");
        if (startTime != null) {
            event.setStartTime(startTime.toLocalDateTime());
        }

        Timestamp endTime = rs.getTimestamp("end_time");
        if (endTime != null) {
            event.setEndTime(endTime.toLocalDateTime());
        }

        Timestamp createTime = rs.getTimestamp("create_time");
        if (createTime != null) {
            event.setCreateTime(createTime.toLocalDateTime());
        }

        return event;
    }

    // 可选：添加实际的日志记录方法
    private void logError(String message, SQLException e, String sql) {
        System.err.println(message);
        System.err.println("SQL: " + sql);
        System.err.println("SQL状态: " + e.getSQLState());
        System.err.println("错误代码: " + e.getErrorCode());
        System.err.println("错误信息: " + e.getMessage());
        e.printStackTrace();
    }
    // 添加通过名称获取事件的方法
    public Event getEventByName(String eventName) {
        String sql = "SELECT * FROM events WHERE event_name = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, eventName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractEventFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}