// model/ArrangementGroup.java
package model;

import java.time.LocalDateTime;
import java.util.List;

public class ArrangementGroup {
    private int groupId;
    private Event event;
    private String groupName;
    private String groupType; // 预赛/复赛/决赛
    private LocalDateTime startTime;
    private List<Enrollment> participants;
    private List<TrackAssignment> assignments;

    // Getters and Setters

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupType() {
        return groupType;
    }

    public void setGroupType(String groupType) {
        this.groupType = groupType;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public List<Enrollment> getParticipants() {
        return participants;
    }

    public void setParticipants(List<Enrollment> participants) {
        this.participants = participants;
    }

    public List<TrackAssignment> getAssignments() {
        return assignments;
    }

    public void setAssignments(List<TrackAssignment> assignments) {
        this.assignments = assignments;
    }
}
