package view;

import controller.AdminController;
import model.User;
import service.CollegeService;
import util.DatabaseUtil;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class AdminMainFrame extends JFrame {
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cardPanel = new JPanel(cardLayout);

    private final AdminController adminController;
    private final User currentUser;
    public AdminMainFrame(User currentUser) {
        // 创建控制器并传递自身引用
        // 保存当前登录的用户
        this.currentUser = currentUser;
        this.adminController = new AdminController(this);

        setTitle("运动会管理系统 - 管理员面板");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initUI();
    }


    private void initUI() {
        // 设置布局
        setLayout(new BorderLayout());

        // 创建卡片面板
        cardPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(cardPanel, BorderLayout.CENTER);

        // 添加仪表盘作为默认卡片
        JPanel dashboardPanel = createDashboardPanel();
        addCard("仪表盘", dashboardPanel);

        // 添加顶部菜单栏
        setJMenuBar(createMenuBar());

        // 显示仪表盘
        showCard("仪表盘");

        // 添加状态栏
        add(createStatusPanel(), BorderLayout.SOUTH);
    }

    // 创建状态栏
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

    // 添加卡片的方法
    public void addCard(String cardName, JPanel panel) {
        cardPanel.add(panel, cardName);
        panel.setName(cardName); // 设置名称以便识别
    }

    // 显示卡片的方法
    public void showCard(String cardName) {
        cardLayout.show(cardPanel, cardName);
    }

    // 移除所有卡片（保留仪表盘）
    public void removeAllCards() {
        Component[] components = cardPanel.getComponents();
        for (Component comp : components) {
            if (!"仪表盘".equals(comp.getName())) {
                cardPanel.remove(comp);
            }
        }
        cardPanel.revalidate();
        cardPanel.repaint();
    }

    private JPanel createDashboardPanel() {
        JPanel dashboardPanel = new JPanel(new GridLayout(2, 3, 20, 20));
        dashboardPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        dashboardPanel.setBackground(Color.WHITE);

        // 用户管理
        dashboardPanel.add(createDashboardCard("用户管理", "👤", "管理用户账户和权限",
                e -> adminController.showUserManagement()));

        // 院系管理
        dashboardPanel.add(createDashboardCard("院系管理", "🏫", "管理院系信息",
                e -> adminController.showCollegeManagement()));

        // 比赛项目管理
        dashboardPanel.add(createDashboardCard("比赛项目管理", "🏅", "管理运动比赛项目",
                e -> adminController.showEventManagement()));

        // 活动安排
        dashboardPanel.add(createDashboardCard("活动安排", "📅", "创建和管理活动",
                e -> adminController.showActivityManagement()));

        // 报名审核 - 新增
        dashboardPanel.add(createDashboardCard("报名审核", "✅", "审核活动报名申请",
                e -> adminController.showApprovalView()));

        // 报名统计
        dashboardPanel.add(createDashboardCard("报名统计", "📊", "查看报名数据分析",
                e -> adminController.showEnrollmentStats()));

        return dashboardPanel;
    }

    // 创建仪表盘卡片
    private JButton createDashboardCard(String title, String icon, String description, ActionListener action) {
        JButton card = new JButton();
        card.setLayout(new BorderLayout(10, 10));
        card.setBackground(new Color(240, 248, 255));
        card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        card.addActionListener(action);
        card.setFocusPainted(false);

        // 卡片顶部（图标和标题）
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        topPanel.setBackground(new Color(240, 248, 255));

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("微软雅黑", Font.PLAIN, 48));
        topPanel.add(iconLabel);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 20));
        topPanel.add(titleLabel);

        card.add(topPanel, BorderLayout.NORTH);

        // 卡片描述
        JTextArea descArea = new JTextArea(description);
        descArea.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        descArea.setEditable(false);
        descArea.setBackground(new Color(240, 248, 255));
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        card.add(descArea, BorderLayout.CENTER);

        return card;
    }

    // 创建菜单栏
    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(new Color(70, 130, 180));
        menuBar.setForeground(Color.WHITE);

        // 用户管理菜单
        JMenu userMenu = new JMenu("用户管理");
        userMenu.setForeground(Color.WHITE);
        userMenu.add(createMenuItem("管理用户账户", e -> adminController.showUserManagement()));
        menuBar.add(userMenu);

        // 院系管理菜单
        JMenu collegeMenu = new JMenu("院系管理");
        collegeMenu.setForeground(Color.WHITE);
        collegeMenu.add(createMenuItem("管理院系信息", e -> adminController.showCollegeManagement()));
        menuBar.add(collegeMenu);

        // 比赛管理菜单
        JMenu eventMenu = new JMenu("比赛管理");
        eventMenu.setForeground(Color.WHITE);
        eventMenu.add(createMenuItem("比赛项目管理", e -> adminController.showEventManagement()));
        eventMenu.add(createMenuItem("活动安排", e -> adminController.showActivityManagement()));
        // 添加报名审核菜单项 - 新增
        eventMenu.add(createMenuItem("报名审核", e -> adminController.showApprovalView()));
        menuBar.add(eventMenu);

        // 数据分析菜单
        JMenu analysisMenu = new JMenu("数据分析");
        analysisMenu.add(createMenuItem("报名统计", e -> adminController.showEnrollmentStats()));
        analysisMenu.add(createMenuItem("比赛编排", e -> adminController.showCompetitionArrangement())); // 新增
        menuBar.add(analysisMenu);

        // 系统菜单
        JMenu systemMenu = new JMenu("系统");
        systemMenu.setForeground(Color.WHITE);
        systemMenu.add(createMenuItem("系统设置", e -> adminController.showSystemSettings()));
        systemMenu.add(createMenuItem("退出系统", e -> System.exit(0)));
        menuBar.add(systemMenu);

        return menuBar;
    }

    // 创建菜单项
    private JMenuItem createMenuItem(String title, ActionListener action) {
        JMenuItem item = new JMenuItem(title);
        item.addActionListener(action);
        return item;
    }
}