package controller;

import view.AdminMainFrame;
import view.CollegeManagementPanel;
import view.EventManagementPanel;
import view.UserManagementPanel;

import javax.swing.*;

public class AdminController {
    private final AdminMainFrame mainFrame;

    public AdminController(AdminMainFrame mainFrame) {
        this.mainFrame = mainFrame;
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