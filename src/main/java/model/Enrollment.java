// model/Enrollment.java
package model;

import java.time.LocalDateTime;

public class Enrollment {
    private int enrollmentId;
    private int userId;
    private int eventId;
    private int status;
    private LocalDateTime enrollTime;
    private Event event;

    // Getters and Setters
    public int getEnrollmentId() { return enrollmentId; }
    public void setEnrollmentId(int enrollmentId) { this.enrollmentId = enrollmentId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getEventId() { return eventId; }
    public void setEventId(int eventId) { this.eventId = eventId; }

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    public LocalDateTime getEnrollTime() { return enrollTime; }
    public void setEnrollTime(LocalDateTime enrollTime) { this.enrollTime = enrollTime; }

    public Event getEvent() { return event; }
    public void setEvent(Event event) { this.event = event; }

    // 状态名称
    public String getStatusName() {
        switch (status) {
            case 0: return "待审核";
            case 1: return "已通过";
            case 2: return "已拒绝";
            default: return "未知状态";
        }
    }
}