package model;

import java.time.LocalDateTime;
import java.sql.Timestamp;

public class Event {
    private int eventId;
    private String eventName;
    private int eventType; // 1-个人, 2-团体
    private String genderLimit; // M-男, F-女, A-不限
    private int minParticipants;
    private int maxParticipants;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String location;
    private String description;
    private int status; // 0-关闭,1-开放报名,2-已结束
    private LocalDateTime createTime; // 添加创建时间

    // 添加常量定义
    public static final int EVENT_TYPE_INDIVIDUAL = 1;
    public static final int EVENT_TYPE_TEAM = 2;

    public static final String GENDER_MALE = "M";
    public static final String GENDER_FEMALE = "F";
    public static final String GENDER_ANY = "A";

    public static final int STATUS_CLOSED = 0;
    public static final int STATUS_OPEN = 1;
    public static final int STATUS_COMPLETED = 2;

    // 构造函数
    public Event() {
        // 设置默认值
        this.eventType = EVENT_TYPE_INDIVIDUAL;
        this.genderLimit = GENDER_ANY;
        this.minParticipants = 1;
        this.maxParticipants = 1;
        this.status = STATUS_OPEN;
        this.createTime = LocalDateTime.now();
    }

    // Getters and Setters

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public int getEventType() {
        return eventType;
    }

    public void setEventType(int eventType) {
        this.eventType = eventType;
    }

    public String getGenderLimit() {
        return genderLimit;
    }

    public void setGenderLimit(String genderLimit) {
        this.genderLimit = genderLimit;
    }

    public int getMinParticipants() {
        return minParticipants;
    }

    public void setMinParticipants(int minParticipants) {
        this.minParticipants = minParticipants;
    }

    public int getMaxParticipants() {
        return maxParticipants;
    }

    public void setMaxParticipants(int maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    // 辅助方法
    public String getEventTypeName() {
        switch (eventType) {
            case EVENT_TYPE_INDIVIDUAL: return "个人";
            case EVENT_TYPE_TEAM: return "团体";
            default: return "未知";
        }
    }

    public String getStatusName() {
        switch (status) {
            case STATUS_CLOSED: return "关闭";
            case STATUS_OPEN: return "开放报名";
            case STATUS_COMPLETED: return "已结束";
            default: return "未知";
        }
    }

    public String getGenderLimitName() {
        switch (genderLimit) {
            case GENDER_MALE: return "男";
            case GENDER_FEMALE: return "女";
            case GENDER_ANY: return "不限";
            default: return "未知";
        }
    }

    // 数据库转换方法
    public void setFromDatabase(Timestamp start, Timestamp end, Timestamp create) {
        if (start != null) this.startTime = start.toLocalDateTime();
        if (end != null) this.endTime = end.toLocalDateTime();
        if (create != null) this.createTime = create.toLocalDateTime();
    }
}