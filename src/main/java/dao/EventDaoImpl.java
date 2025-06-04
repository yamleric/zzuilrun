package dao;

import model.Event;
import util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EventDaoImpl implements EventDao {
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
            e.printStackTrace();
        }
        return events;
    }

    @Override
    public int insertEvent(Event event) {
        String sql = "INSERT INTO events (event_name, event_type, gender_limit, min_participants, "
                + "max_participants, start_time, end_time, location, description, status) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            setEventParameters(stmt, event);
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public boolean updateEvent(Event event) {
        String sql = "UPDATE events SET event_name = ?, event_type = ?, gender_limit = ?, "
                + "min_participants = ?, max_participants = ?, start_time = ?, end_time = ?, "
                + "location = ?, description = ?, status = ? WHERE event_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            setEventParameters(stmt, event);
            stmt.setInt(11, event.getEventId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
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

    // 在 setEventParameters 方法中：
    private void setEventParameters(PreparedStatement stmt, Event event) throws SQLException {
        // ... 其他字段设置 ...

        // 处理时间字段
        stmt.setTimestamp(6, event.getStartTime() != null ?
                Timestamp.valueOf(event.getStartTime()) : null);
        stmt.setTimestamp(7, event.getEndTime() != null ?
                Timestamp.valueOf(event.getEndTime()) : null);

        // 对于创建时间，插入时使用当前时间
        if (event.getEventId() == 0) { // 新增记录
            stmt.setTimestamp(10, new Timestamp(System.currentTimeMillis()));
        } else { // 更新记录
            stmt.setTimestamp(10, Timestamp.valueOf(event.getCreateTime()));
        }
    }
    // 在 mapRowToEvent 方法中：
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

        // 处理时间字段转换
        event.setFromDatabase(
                rs.getTimestamp("start_time"),
                rs.getTimestamp("end_time"),
                rs.getTimestamp("create_time")
        );

        return event;
    }

}