package view;

import controller.AdminController;
import model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class AdminMainFrame extends JFrame {
    private final User currentUser;
    private final AdminController adminController;

    public AdminMainFrame(User user) {
        this.currentUser = user;
        this.adminController = new AdminController();
//        initUI();
    }

    private void initUI() {
        setTitle("管理员主界面 - " + currentUser.getRealName());
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 顶部菜单栏
        JMenuBar menuBar = createMenuBar();
        setJMenuBar(menuBar);

        // 欢迎面板
        JPanel welcomePanel = new JPanel();
        welcomePanel.setLayout(new BoxLayout(welcomePanel, BoxLayout.Y_AXIS));
        welcomePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel welcomeLabel = new JLabel("管理员: " + currentUser.getRealName());
        welcomeLabel.setFont(new Font("宋体", Font.BOLD, 24));
        welcomeLabel.setAlignmentX(CENTER_ALIGNMENT);

        JLabel systemLabel = new JLabel("活动报名管理系统");
        systemLabel.setFont(new Font("宋体", Font.PLAIN, 16));
        systemLabel.setAlignmentX(CENTER_ALIGNMENT);

        welcomePanel.add(welcomeLabel);
        welcomePanel.add(Box.createVerticalStrut(10));
        welcomePanel.add(systemLabel);

        // 仪表盘面板
        JPanel dashboardPanel = new JPanel();
        dashboardPanel.setLayout(new GridLayout(2, 2, 20, 20));
        dashboardPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        dashboardPanel.add(createDashboardCard("用户管理", "user_icon.png",
                e -> adminController.showUserManagement()));
        dashboardPanel.add(createDashboardCard("活动管理", "event_icon.png",
                e -> adminController.showActivityManagement()));
        dashboardPanel.add(createDashboardCard("报名统计", "stats_icon.png",
                e -> adminController.showEnrollmentStats()));
        dashboardPanel.add(createDashboardCard("系统设置", "settings_icon.png",
                e -> adminController.showSystemSettings()));

        // 状态栏
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBorder(BorderFactory.createEtchedBorder());

        JLabel statusLabel = new JLabel("系统状态: 运行中 | 登录时间: " + new java.util.Date());
        statusPanel.add(statusLabel, BorderLayout.WEST);

        // 添加所有组件
        add(welcomePanel, BorderLayout.NORTH);
        add(dashboardPanel, BorderLayout.CENTER);
        add(statusPanel, BorderLayout.SOUTH);
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // 系统菜单
        JMenu systemMenu = new JMenu("系统");
        JMenuItem settingsItem = new JMenuItem("系统设置");
        JMenuItem logoutItem = new JMenuItem("退出登录");
        systemMenu.add(settingsItem);
        systemMenu.addSeparator();
        systemMenu.add(logoutItem);

        // 管理菜单
        JMenu manageMenu = new JMenu("管理");
        JMenuItem usersItem = new JMenuItem("用户管理");
        JMenuItem eventsItem = new JMenuItem("活动管理");
        manageMenu.add(usersItem);
        manageMenu.add(eventsItem);

        // 报名菜单
        JMenu enrollmentMenu = new JMenu("报名");
        JMenuItem statsItem = new JMenuItem("报名统计");
        JMenuItem arrangeItem = new JMenuItem("比赛编排");
        enrollmentMenu.add(statsItem);
        enrollmentMenu.add(arrangeItem);

        // 工具菜单
        JMenu toolsMenu = new JMenu("工具");
        JMenuItem reportItem = new JMenuItem("生成报表");
        JMenuItem exportItem = new JMenuItem("导出数据");
        toolsMenu.add(reportItem);
        toolsMenu.add(exportItem);

        // 添加菜单项事件
        logoutItem.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });
        usersItem.addActionListener(e -> adminController.showUserManagement());
        eventsItem.addActionListener(e -> adminController.showActivityManagement());
        statsItem.addActionListener(e -> adminController.showEnrollmentStats());
        arrangeItem.addActionListener(e -> adminController.showCompetitionArrangement());

        // 比赛编排只在报名结束后启用
        arrangeItem.setEnabled(isEnrollmentPhaseOver());

        // 添加菜单到菜单栏
        menuBar.add(systemMenu);
        menuBar.add(manageMenu);
        menuBar.add(enrollmentMenu);
        menuBar.add(toolsMenu);

        return menuBar;
    }

    private JPanel createDashboardCard(String title, String iconFile, java.awt.event.ActionListener action) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createRaisedBevelBorder(),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        card.setBackground(Color.WHITE);

        // 图标（暂时用占位符）
        JLabel iconLabel = new JLabel(new ImageIcon(getClass().getResource("/icons/" + iconFile)));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(iconLabel, BorderLayout.CENTER);

        JButton titleButton = new JButton(title);
        titleButton.addActionListener(action);
        titleButton.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(titleButton, BorderLayout.SOUTH);

        return card;
    }

    // 检查报名阶段是否结束
    private boolean isEnrollmentPhaseOver() {
        // 实际实现应从数据库或配置中获取
        // 这里返回true表示报名已结束，可进行比赛编排
        return true;
    }
}