package controller;

import model.Enrollment;
import service.ApprovalService;
import service.ApprovalServiceImpl;
import view.AdminMainFrame;
import view.ApprovalView;
import view.CollegeManagementPanel;
import view.EventManagementPanel;
import view.UserManagementPanel;

import javax.swing.*;
import java.util.List;

public class AdminController {
    private final AdminMainFrame mainFrame;
    private final ApprovalService approvalService = new ApprovalServiceImpl();

    public AdminController(AdminMainFrame mainFrame) {
        this.mainFrame = mainFrame;
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

    public void showEnrollmentStats() {
        mainFrame.removeAllCards();
        JOptionPane.showMessageDialog(mainFrame, "报名统计功能开发中");
    }

    public void showSystemSettings() {
        mainFrame.removeAllCards();
        JOptionPane.showMessageDialog(mainFrame, "系统设置功能开发中");
    }

    public void showCompetitionArrangement() {
        mainFrame.removeAllCards();
        JOptionPane.showMessageDialog(mainFrame, "比赛编排功能开发中");
    }

    public void showEventManagement() {
        mainFrame.removeAllCards();
        // 创建事件管理面板
        EventManagementPanel eventPanel = new EventManagementPanel(this);
        mainFrame.addCard("比赛项目管理", eventPanel);
        mainFrame.showCard("比赛项目管理");
    }
}