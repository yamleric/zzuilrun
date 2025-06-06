package model;

import java.time.LocalDateTime;

public class Enrollment {
    private int enrollmentId;
    private int userId;
    private String username;
    private String realName;
    private int eventId;
    private String eventName;
    private int status;
    private LocalDateTime enrollTime;

    // Getters and Setters
    public int getEnrollmentId() { return enrollmentId; }
    public void setEnrollmentId(int enrollmentId) { this.enrollmentId = enrollmentId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getRealName() { return realName; }
    public void setRealName(String realName) { this.realName = realName; }

    public int getEventId() { return eventId; }
    public void setEventId(int eventId) { this.eventId = eventId; }

    public String getEventName() { return eventName; }
    public void setEventName(String eventName) { this.eventName = eventName; }

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    public LocalDateTime getEnrollTime() { return enrollTime; }
    public void setEnrollTime(LocalDateTime enrollTime) { this.enrollTime = enrollTime; }

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