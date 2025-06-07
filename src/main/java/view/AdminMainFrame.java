package view;

import controller.AdminController;
import model.User;
import util.DatabaseUtil;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class AdminMainFrame extends JFrame {
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cardPanel = new JPanel(cardLayout);
    private final AdminController adminController;
    private final User currentUser;

    // 现代配色方案
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color SECONDARY_COLOR = new Color(52, 152, 219);
    private static final Color ACCENT_COLOR = new Color(46, 204, 113);
    private static final Color BACKGROUND_COLOR = new Color(245, 245, 245);
    private static final Color CARD_HOVER_COLOR = new Color(230, 240, 255);
    private static final Font TITLE_FONT = new Font("微软雅黑", Font.BOLD, 24);
    private static final Font SUBTITLE_FONT = new Font("微软雅黑", Font.PLAIN, 14);
    private static final Font MENU_FONT = new Font("微软雅黑", Font.PLAIN, 14);
    private static final Font CARD_TITLE_FONT = new Font("微软雅黑", Font.BOLD, 18);
    private static final Font CARD_DESC_FONT = new Font("微软雅黑", Font.PLAIN, 14);

    public AdminMainFrame(User currentUser) {
        this.currentUser = currentUser;
        this.adminController = new AdminController(this);

        setTitle("运动会管理系统 - 管理员面板");
        setSize(1200, 1000); // 稍大尺寸提供更好的布局空间
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initUI();
    }

    private void initUI() {
        // 使用更现代的布局
        setLayout(new BorderLayout());
        getContentPane().setBackground(BACKGROUND_COLOR);

        // 添加顶部标题栏
        add(createHeaderPanel(), BorderLayout.NORTH);

        // 创建卡片面板
        cardPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        cardPanel.setBackground(BACKGROUND_COLOR);
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

    // 创建顶部标题栏
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));

        JLabel titleLabel = new JLabel("运动会管理系统");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(Color.WHITE);

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        userPanel.setOpaque(false);

        JLabel userLabel = new JLabel("管理员: " + currentUser.getRealName());
        userLabel.setFont(SUBTITLE_FONT);
        userLabel.setForeground(Color.WHITE);
        userLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));

        JLabel roleLabel = new JLabel("角色: 管理员");
        roleLabel.setFont(SUBTITLE_FONT);
        roleLabel.setForeground(Color.WHITE);

        userPanel.add(userLabel);
        userPanel.add(roleLabel);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(userPanel, BorderLayout.EAST);

        return headerPanel;
    }

    // 创建状态栏
    private JPanel createStatusPanel() {
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(1, 0, 0, 0, new Color(220, 220, 220)),
                new EmptyBorder(5, 15, 5, 15)
        ));
        statusPanel.setBackground(new Color(250, 250, 250));

        JLabel statusLabel = new JLabel("系统状态: 运行中 | 登录时间: " + new java.util.Date());
        statusLabel.setFont(MENU_FONT);
        statusLabel.setForeground(new Color(100, 100, 100));
        statusPanel.add(statusLabel, BorderLayout.WEST);

        JLabel copyrightLabel = new JLabel("© 2025 运动会管理系统", SwingConstants.RIGHT);
        copyrightLabel.setFont(MENU_FONT);
        copyrightLabel.setForeground(new Color(100, 100, 100));
        statusPanel.add(copyrightLabel, BorderLayout.EAST);

        return statusPanel;
    }

    // 主仪表盘面板
    private JPanel createDashboardPanel() {
        JPanel dashboardPanel = new JPanel();
        dashboardPanel.setLayout(new BoxLayout(dashboardPanel, BoxLayout.Y_AXIS));
        dashboardPanel.setBackground(BACKGROUND_COLOR);
        dashboardPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 添加欢迎标题
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setOpaque(false);
        titlePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        JLabel titleLabel = new JLabel("系统概览");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 22));
        titleLabel.setForeground(new Color(70, 70, 70));
        titlePanel.add(titleLabel);

        dashboardPanel.add(titlePanel);

        // 添加功能卡片区域
        JPanel cardGridPanel = new JPanel(new GridLayout(2, 3, 25, 25));
        cardGridPanel.setOpaque(false);
        cardGridPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 20, 0));

        // 用户管理卡片
        cardGridPanel.add(createDashboardCard("用户管理", "👤", "管理用户账户和权限",
                PRIMARY_COLOR, e -> adminController.showUserManagement()));

        // 院系管理卡片
        cardGridPanel.add(createDashboardCard("院系管理", "🏫", "管理院系基本信息",
                new Color(155, 89, 182), e -> adminController.showCollegeManagement()));

        // 比赛项目管理卡片
        cardGridPanel.add(createDashboardCard("比赛项目管理", "🏅", "管理运动会比赛项目",
                new Color(230, 126, 34), e -> adminController.showEventManagement()));

        // 活动安排卡片
        cardGridPanel.add(createDashboardCard("活动安排", "📅", "创建和管理运动会日程",
                new Color(46, 204, 113), e -> adminController.showActivityManagement()));

        // 报名审核卡片
        cardGridPanel.add(createDashboardCard("报名审核", "✅", "审核学生报名申请",
                new Color(241, 196, 15), e -> adminController.showApprovalView()));

        // 报名统计卡片
        cardGridPanel.add(createDashboardCard("报名统计", "📊", "查看报名数据分析",
                new Color(52, 152, 219), e -> adminController.showEnrollmentStats()));

        dashboardPanel.add(cardGridPanel);

        // 添加统计数据区域
        JPanel statsPanel = createStatsPanel();
        dashboardPanel.add(statsPanel);

        return dashboardPanel;
    }

    // 创建功能卡片
    private JPanel createDashboardCard(String title, String icon, String description,
                                       Color color, ActionListener action) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230)),
                new EmptyBorder(25, 20, 25, 20)
        ));

        // 添加悬停效果
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBackground(CARD_HOVER_COLOR);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                card.setBackground(Color.WHITE);
            }
        });

        // 顶部区域 (图标)
        JPanel topPanel = new JPanel();
        topPanel.setOpaque(false);
        topPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        iconLabel.setForeground(color);
        topPanel.add(iconLabel);

        // 中部区域 (标题和描述)
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(CARD_TITLE_FONT);
        titleLabel.setForeground(new Color(70, 70, 70));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextArea descArea = new JTextArea(description);
        descArea.setFont(CARD_DESC_FONT);
        descArea.setForeground(new Color(120, 120, 120));
        descArea.setEditable(false);
        descArea.setOpaque(false);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setAlignmentX(Component.CENTER_ALIGNMENT);
        descArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));

        centerPanel.add(titleLabel);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(descArea);

        // 底部区域 (操作按钮)
        JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        JButton actionButton = new JButton("进入");
        actionButton.setFont(new Font("微软雅黑", Font.BOLD, 14));
        actionButton.setForeground(Color.WHITE);
        actionButton.setBackground(color);
        actionButton.setFocusPainted(false);
        actionButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        actionButton.addActionListener(action);
        actionButton.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        bottomPanel.add(actionButton);

        // 添加到卡片
        card.add(topPanel, BorderLayout.NORTH);
        card.add(centerPanel, BorderLayout.CENTER);
        card.add(bottomPanel, BorderLayout.SOUTH);

        // 添加点击事件
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                actionButton.doClick();
            }
        });

        return card;
    }

    // 创建统计面板
    private JPanel createStatsPanel() {
        JPanel statsPanel = new JPanel(new BorderLayout());
        statsPanel.setOpaque(false);
        statsPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        statsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        JPanel container = new JPanel(new GridLayout(1, 3, 15, 0));
        container.setOpaque(false);

        // 统计项1
        container.add(createStatCard("用户总数", "3,248", PRIMARY_COLOR));
        // 统计项2
        container.add(createStatCard("报名总数", "12,875", ACCENT_COLOR));
        // 统计项3
        container.add(createStatCard("活动数量", "36", SECONDARY_COLOR));

        statsPanel.add(container);
        return statsPanel;
    }

    // 创建统计卡片
    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230)),
                new EmptyBorder(20, 20, 20, 20)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        titleLabel.setForeground(new Color(120, 120, 120));

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("微软雅黑", Font.BOLD, 28));
        valueLabel.setForeground(color);

        JProgressBar progressBar = new JProgressBar();
        progressBar.setValue(65); // 示例进度
        progressBar.setStringPainted(false);
        progressBar.setForeground(color);
        progressBar.setBackground(new Color(240, 240, 240));
        progressBar.setBorder(BorderFactory.createEmptyBorder());
        progressBar.setPreferredSize(new Dimension(progressBar.getPreferredSize().width, 8));

        card.add(titleLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(valueLabel);
        card.add(Box.createVerticalStrut(15));
        card.add(progressBar);

        return card;
    }

    // 创建菜单栏
    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(Color.WHITE);
        menuBar.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0, 0, 1, 0, new Color(240, 240, 240)),
                new EmptyBorder(5, 10, 5, 10)
        ));

        // 用户管理菜单
        JMenu userMenu = createMenu("用户管理", e -> adminController.showUserManagement());
        menuBar.add(userMenu);

        // 院系管理菜单
        JMenu collegeMenu = createMenu("院系管理", e -> adminController.showCollegeManagement());
        menuBar.add(collegeMenu);

        // 比赛管理菜单
        JMenu eventMenu = createMenu("比赛管理");
        eventMenu.add(createMenuItem("比赛项目管理", e -> adminController.showEventManagement()));
        eventMenu.add(createMenuItem("活动安排", e -> adminController.showActivityManagement()));
        eventMenu.add(createMenuItem("报名审核", e -> adminController.showApprovalView()));
        menuBar.add(eventMenu);

        // 数据分析菜单
        JMenu analysisMenu = createMenu("数据分析");
        analysisMenu.add(createMenuItem("报名统计", e -> adminController.showEnrollmentStats()));
        analysisMenu.add(createMenuItem("比赛编排", e -> adminController.showCompetitionArrangement()));
        menuBar.add(analysisMenu);

        // 系统菜单
        JMenu systemMenu = createMenu("系统");
        systemMenu.add(createMenuItem("系统设置", e -> adminController.showSystemSettings()));
        systemMenu.add(createMenuItem("退出系统", e -> System.exit(0)));
        menuBar.add(systemMenu);

        return menuBar;
    }

    // 创建菜单项
    private JMenuItem createMenuItem(String title, ActionListener action) {
        JMenuItem item = new JMenuItem(title);
        item.setFont(MENU_FONT);
        item.setForeground(new Color(80, 80, 80));
        item.addActionListener(action);
        return item;
    }

    // 创建菜单
    private JMenu createMenu(String title) {
        JMenu menu = new JMenu(title);
        menu.setFont(MENU_FONT);
        menu.setForeground(new Color(70, 70, 70));
        return menu;
    }

    // 创建带点击事件的菜单
    private JMenu createMenu(String title, ActionListener action) {
        JMenu menu = createMenu(title);
        menu.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                action.actionPerformed(null);
            }
        });
        return menu;
    }

    // 添加卡片的方法
    public void addCard(String cardName, JPanel panel) {
        cardPanel.add(panel, cardName);
        panel.setName(cardName);
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

    // 显示报名统计面板
    public void showEnrollmentStats() {
        this.removeAllCards();
        EnrollmentStatsPanel statsPanel = new EnrollmentStatsPanel(adminController);
        this.addCard("报名统计", statsPanel);
        this.showCard("报名统计");
    }
}