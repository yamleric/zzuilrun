package controller;

import model.*;
import service.ApprovalService;
import service.ApprovalServiceImpl;
import service.ArrangementService; // 添加ArrangementService导入
import service.EnrollmentStatsService;
import view.*;

import javax.swing.*;
import java.time.LocalDateTime;
import java.util.List;

public class AdminController {
    private final AdminMainFrame mainFrame;
    private final ApprovalService approvalService = new ApprovalServiceImpl();
    private final ArrangementService arrangementService = new ArrangementService(); // 声明并初始化ArrangementService
    private final EnrollmentStatsService statsService = new EnrollmentStatsService();

    public AdminController(AdminMainFrame mainFrame) {
        this.mainFrame = mainFrame;
    }

    // 获取项目报名统计
    public List<EnrollmentStats> getEventEnrollmentStats() {
        return statsService.getEventEnrollmentStats();
    }

    // 获取学院报名统计
    public List<EnrollmentStats> getCollegeEnrollmentStats() {
        return statsService.getCollegeEnrollmentStats();
    }
    // 在 AdminController.java 中添加
    public void showEnrollmentStats() {
        mainFrame.showEnrollmentStats();
    }
    // 显示报名审核视图
    public void showApprovalView() {
        mainFrame.removeAllCards();
        // 创建报名审核面板
        ApprovalView approvalView = new ApprovalView(this);
        mainFrame.addCard("报名审核", approvalView);
        mainFrame.showCard("报名审核");
    }

    // 获取待审核报名列表
    public List<Enrollment> getPendingEnrollments() {
        return approvalService.getPendingEnrollments();
    }

    // 批准报名
    public boolean approveEnrollment(int enrollmentId) {
        return approvalService.approveEnrollment(enrollmentId);
    }

    // 拒绝报名
    public boolean rejectEnrollment(int enrollmentId) {
        return approvalService.rejectEnrollment(enrollmentId);
    }

    public void showUserManagement() {
        mainFrame.removeAllCards();
        // 创建用户管理面板
        UserManagementPanel userPanel = new UserManagementPanel(this);
        mainFrame.addCard("用户管理", userPanel);
        mainFrame.showCard("用户管理");
    }

    public void showCollegeManagement() {
        mainFrame.removeAllCards();
        // 创建院系管理面板
        CollegeManagementPanel collegePanel = new CollegeManagementPanel(this);
        mainFrame.addCard("院系管理", collegePanel);
        mainFrame.showCard("院系管理");
    }

    public void showActivityManagement() {
        mainFrame.removeAllCards();
        JOptionPane.showMessageDialog(mainFrame, "活动管理功能开发中");
    }

//    public void showEnrollmentStats() {
//        mainFrame.removeAllCards();
//        JOptionPane.showMessageDialog(mainFrame, "报名统计功能开发中");
//    }

    public void showSystemSettings() {
        mainFrame.removeAllCards();
        JOptionPane.showMessageDialog(mainFrame, "系统设置功能开发中");
    }

//    public void showCompetitionArrangement() {
//        mainFrame.removeAllCards();
//        JOptionPane.showMessageDialog(mainFrame, "比赛编排功能开发中");
//    }

    public void showEventManagement() {
        mainFrame.removeAllCards();
        // 创建事件管理面板
        EventManagementPanel eventPanel = new EventManagementPanel(this);
        mainFrame.addCard("比赛项目管理", eventPanel);
        mainFrame.showCard("比赛项目管理");
    }
    public void showCompetitionArrangement() {
        mainFrame.removeAllCards();
        ArrangementPanel arrangementPanel = new ArrangementPanel(this);
        mainFrame.addCard("比赛编排", arrangementPanel);
        mainFrame.showCard("比赛编排");
    }
    // 获取需要编排的项目
    public List<Event> getEventsForArrangement() {
        return arrangementService.getEventsForArrangement();
    }

    // 自动分组
    public List<ArrangementGroup> autoGroup(Event event) {
        List<Enrollment> enrollments = arrangementService.getApprovedEnrollments(event.getEventId());
        return arrangementService.autoGroup(event, enrollments);
    }

    // 分配道次
    public List<TrackAssignment> assignTracks(ArrangementGroup group) {
        return arrangementService.assignTracks(group);
    }

    // 保存编排结果
    public void saveArrangement(List<ArrangementGroup> groups) {
        arrangementService.saveArrangement(groups);
    }

    // 生成比赛名单
    public void generateStartList(Event event) {
        arrangementService.generateStartList(event);
    }

    // 设置比赛时间
    public void setGroupTime(ArrangementGroup group, LocalDateTime startTime) {
        group.setStartTime(startTime);
    }
}