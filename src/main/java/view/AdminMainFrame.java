package view;

import controller.AdminController;
import model.User;
import service.CollegeService;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class AdminMainFrame extends JFrame {
    private final User currentUser;
    private final AdminController adminController;
    private final CollegeService collegeService = new CollegeService();

    public AdminMainFrame(User user) {
        this.currentUser = user;
        this.adminController = new AdminController();
        initUI();
    }

    private void initUI() {
        setTitle("管理员主界面 - " + currentUser.getRealName());
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 设置整体背景色
        getContentPane().setBackground(new Color(245, 245, 255));

        // 顶部菜单栏
        JMenuBar menuBar = createMenuBar();
        setJMenuBar(menuBar);

        // 欢迎面板
        JPanel welcomePanel = createWelcomePanel();
        add(welcomePanel, BorderLayout.NORTH);

        // 仪表盘面板
        JPanel dashboardPanel = createDashboardPanel();
        add(dashboardPanel, BorderLayout.CENTER);

        // 状态栏
        JPanel statusPanel = createStatusPanel();
        add(statusPanel, BorderLayout.SOUTH);
    }

    private JPanel createWelcomePanel() {
        JPanel welcomePanel = new JPanel();
        welcomePanel.setLayout(new BoxLayout(welcomePanel, BoxLayout.Y_AXIS));
        welcomePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        welcomePanel.setOpaque(false);

        JLabel welcomeLabel = new JLabel("管理员: " + currentUser.getRealName());
        welcomeLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));
        welcomeLabel.setAlignmentX(CENTER_ALIGNMENT);

        JLabel systemLabel = new JLabel("活动报名管理系统");
        systemLabel.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        systemLabel.setAlignmentX(CENTER_ALIGNMENT);
        systemLabel.setForeground(new Color(100, 100, 150));

        welcomePanel.add(welcomeLabel);
        welcomePanel.add(Box.createVerticalStrut(10));
        welcomePanel.add(systemLabel);
        return welcomePanel;
    }

    private JPanel createDashboardPanel() {
        JPanel dashboardPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        dashboardPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        dashboardPanel.setOpaque(false);

        dashboardPanel.add(createDashboardCard("用户管理", "👤", "管理用户账户和权限"));
        dashboardPanel.add(createDashboardCard("活动管理", "📅", "创建和管理活动"));
        dashboardPanel.add(createDashboardCard("报名统计", "📊", "查看报名数据分析"));
        dashboardPanel.add(createDashboardCard("系统设置", "⚙️", "配置系统参数"));

        return dashboardPanel;
    }

    private JPanel createStatusPanel() {
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBorder(BorderFactory.createEtchedBorder());
        statusPanel.setBackground(new Color(240, 240, 245));

        JLabel statusLabel = new JLabel("系统状态: 运行中 | 登录时间: " + new java.util.Date());
        statusLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        statusPanel.add(statusLabel, BorderLayout.WEST);

        JLabel copyrightLabel = new JLabel("© 2025 活动报名系统", SwingConstants.RIGHT);
        copyrightLabel.setFont(new Font("微软雅黑", Font.PLAIN, 11));
        statusPanel.add(copyrightLabel, BorderLayout.EAST);

        return statusPanel;
    }

    private JButton createDashboardCard(String title, String iconSymbol, String description) {
        JButton button = new JButton(
                "<html><center><div style='font-size: 32pt; margin-bottom: 8px;'>" + iconSymbol +
                        "</div><b style='font-size: 16pt;'>" + title + "</b><br>" +
                        "<small style='color: #666; font-size: 11pt;'>" + description + "</small></center></html>"
        );

        button.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        button.setBackground(Color.WHITE);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 220), 1),
                BorderFactory.createEmptyBorder(20, 10, 25, 10)
        ));
        button.setMargin(new Insets(10, 10, 10, 10));

        button.addMouseListener(new MouseAdapter() {
            Color originalBg = button.getBackground();

            @Override
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(new Color(245, 245, 255));
                button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent evt) {
                button.setBackground(originalBg);
            }
        });

        // 添加点击事件
        switch(title) {
            case "用户管理":
                button.addActionListener(e -> adminController.showUserManagement());
                break;
            case "活动管理":
                button.addActionListener(e -> adminController.showActivityManagement());
                break;
            case "报名统计":
                button.addActionListener(e -> adminController.showEnrollmentStats());
                break;
            case "系统设置":
                button.addActionListener(e -> adminController.showSystemSettings());
                break;
        }

        return button;
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(new Color(70, 130, 180));
        menuBar.setForeground(Color.WHITE);

        // 系统菜单
        JMenu systemMenu = createSystemMenu();

        // 管理菜单
        JMenu manageMenu = createManageMenu();

        // 院系管理菜单
        JMenu collegeMenu = new JMenu("院系管理");
        collegeMenu.setForeground(Color.WHITE);

        // 添加院系管理菜单项
        JMenuItem manageCollegesItem = new JMenuItem("管理院系列表");
        manageCollegesItem.addActionListener(e -> showEnhancedCollegeManagement());
        collegeMenu.add(manageCollegesItem);

        // 报名菜单
        JMenu enrollmentMenu = new JMenu("报名");
        enrollmentMenu.setForeground(Color.WHITE);

        JMenuItem statsItem = new JMenuItem("报名统计");
        JMenuItem arrangeItem = new JMenuItem("比赛编排");
        enrollmentMenu.add(statsItem);
        enrollmentMenu.add(arrangeItem);

        // 工具菜单
        JMenu toolsMenu = new JMenu("工具");
        toolsMenu.setForeground(Color.WHITE);

        JMenuItem reportItem = new JMenuItem("生成报表");
        JMenuItem exportItem = new JMenuItem("导出数据");
        toolsMenu.add(reportItem);
        toolsMenu.add(exportItem);

        // 添加菜单项事件
        manageMenu.addSeparator();
        statsItem.addActionListener(e -> adminController.showEnrollmentStats());
        arrangeItem.addActionListener(e -> adminController.showCompetitionArrangement());

        // 添加菜单到菜单栏
        menuBar.add(systemMenu);
        menuBar.add(manageMenu);
        menuBar.add(collegeMenu);
        menuBar.add(enrollmentMenu);
        menuBar.add(toolsMenu);

        return menuBar;
    }

    private JMenu createSystemMenu() {
        JMenu systemMenu = new JMenu("系统");
        systemMenu.setForeground(Color.WHITE);

        JMenuItem settingsItem = new JMenuItem("系统设置");
        JMenuItem logoutItem = new JMenuItem("退出登录");
        systemMenu.add(settingsItem);
        systemMenu.addSeparator();
        systemMenu.add(logoutItem);

        logoutItem.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });

        return systemMenu;
    }

    private JMenu createManageMenu() {
        JMenu manageMenu = new JMenu("管理");
        manageMenu.setForeground(Color.WHITE);

        JMenuItem usersItem = new JMenuItem("用户管理");
        JMenuItem eventsItem = new JMenuItem("活动管理");
        manageMenu.add(usersItem);
        manageMenu.add(eventsItem);

        usersItem.addActionListener(e -> adminController.showUserManagement());
        eventsItem.addActionListener(e -> adminController.showActivityManagement());

        return manageMenu;
    }

    // 优化后的院系管理界面
    private void showEnhancedCollegeManagement() {
        JDialog dialog = new JDialog(this, "院系管理", true);
        dialog.setSize(650, 450);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setLocationRelativeTo(this);

        // 主面板
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // 标题面板
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        JLabel titleLabel = new JLabel("院系列表管理");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 18));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("添加、编辑或删除系统中的院系");
        subtitleLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        subtitleLabel.setForeground(Color.GRAY);
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        titlePanel.add(titleLabel);
        titlePanel.add(Box.createVerticalStrut(5));
        titlePanel.add(subtitleLabel);

        // 院系列表面板
        JPanel listPanel = new JPanel(new BorderLayout());
        listPanel.setBorder(BorderFactory.createTitledBorder("院系列表"));

        List<String> colleges = collegeService.getAllColleges();
        DefaultListModel<String> listModel = new DefaultListModel<>();
        for (String college : colleges) {
            listModel.addElement(college);
        }

        JList<String> collegeList = new JList<>(listModel);
        collegeList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        collegeList.setFont(new Font("微软雅黑", Font.PLAIN, 14));

        JScrollPane scrollPane = new JScrollPane(collegeList);
        listPanel.add(scrollPane, BorderLayout.CENTER);

        // 操作按钮面板
        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JButton deleteButton = new JButton("删除选中");
        deleteButton.setBackground(new Color(220, 80, 60)); // 红色背景
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setEnabled(false);

        JButton addButton = new JButton("添加院系");
        JButton refreshButton = new JButton("刷新列表");

        buttonPanel.add(deleteButton);
        buttonPanel.add(addButton);
        buttonPanel.add(refreshButton);

        // 输入面板
        JPanel inputPanel = new JPanel(new BorderLayout(10, 0));
        inputPanel.setBorder(BorderFactory.createTitledBorder("添加新院系"));

        JTextField newCollegeField = new JTextField();
        newCollegeField.setFont(new Font("微软雅黑", Font.PLAIN, 14));

        JButton confirmAddButton = new JButton("确认添加");
        confirmAddButton.setBackground(new Color(60, 140, 100)); // 绿色背景
        confirmAddButton.setForeground(Color.WHITE);

        inputPanel.add(newCollegeField, BorderLayout.CENTER);
        inputPanel.add(confirmAddButton, BorderLayout.EAST);

        // 功能面板（操作按钮+输入面板）
        JPanel functionPanel = new JPanel();
        functionPanel.setLayout(new BoxLayout(functionPanel, BoxLayout.Y_AXIS));
        functionPanel.add(buttonPanel);
        functionPanel.add(Box.createVerticalStrut(15));
        functionPanel.add(inputPanel);

        // 信息面板（统计信息）
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        infoPanel.setBorder(BorderFactory.createTitledBorder("统计信息"));

        JLabel countLabel = new JLabel("院系总数: " + colleges.size());
        countLabel.setFont(new Font("微软雅黑", Font.PLAIN, 13));

        infoPanel.add(countLabel);

        // 添加到主面板
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(listPanel, BorderLayout.CENTER);
        mainPanel.add(functionPanel, BorderLayout.SOUTH);

        // 添加到对话框
        dialog.add(mainPanel, BorderLayout.CENTER);
        dialog.add(infoPanel, BorderLayout.SOUTH);

        // 事件监听器
        collegeList.addListSelectionListener(e -> {
            boolean hasSelection = !collegeList.isSelectionEmpty();
            deleteButton.setEnabled(hasSelection);
        });

        confirmAddButton.addActionListener(e -> {
            String collegeName = newCollegeField.getText().trim();
            if (!collegeName.isEmpty()) {
                if (collegeService.addCollege(collegeName)) {
                    listModel.addElement(collegeName);
                    newCollegeField.setText("");
                    countLabel.setText("院系总数: " + listModel.size());
                    JOptionPane.showMessageDialog(dialog, "院系添加成功！");
                } else {
                    JOptionPane.showMessageDialog(dialog, "添加失败: 院系名称已存在", "添加失败", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(dialog, "请输入院系名称", "输入错误", JOptionPane.WARNING_MESSAGE);
            }
        });

        // 修改后的删除按钮事件监听器
        deleteButton.addActionListener(e -> {
            int[] selectedIndices = collegeList.getSelectedIndices();
            if (selectedIndices.length > 0) {
                int confirm = JOptionPane.showConfirmDialog(
                        dialog,
                        "确定要删除选中的 " + selectedIndices.length + " 个院系吗？",
                        "确认删除",
                        JOptionPane.YES_NO_OPTION
                );

                if (confirm == JOptionPane.YES_OPTION) {
                    // 从后向前删除避免索引变化
                    for (int i = selectedIndices.length - 1; i >= 0; i--) {
                        String college = listModel.get(selectedIndices[i]);
                        if (collegeService.deleteCollege(college)) {
                            listModel.remove(selectedIndices[i]);
                        } else {
                            JOptionPane.showMessageDialog(
                                    dialog,
                                    "删除 '" + college + "' 失败！该院系可能有关联数据",
                                    "删除失败",
                                    JOptionPane.WARNING_MESSAGE
                            );
                        }
                    }
                    countLabel.setText("院系总数: " + listModel.size());
                }
            }
        });

        refreshButton.addActionListener(e -> {
            List<String> updatedColleges = collegeService.getAllColleges();
            listModel.clear();
            for (String college : updatedColleges) {
                listModel.addElement(college);
            }
            countLabel.setText("院系总数: " + updatedColleges.size());
            collegeList.clearSelection();
        });

        dialog.setVisible(true);
    }

    private boolean isEnrollmentPhaseOver() {
        // 实际实现应从数据库或配置中获取
        return true;
    }
}