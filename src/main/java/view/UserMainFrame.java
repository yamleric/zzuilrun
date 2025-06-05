package view;

import controller.StudentController;
import model.User;
import service.EnrollmentService;
import util.DatabaseUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class UserMainFrame extends JFrame {
    private final User currentUser;
    private final StudentController studentController;
    private JLabel statusLabel; // 保存状态标签引用以便更新

    public UserMainFrame(User user) {
        this.currentUser = user;
        this.studentController = new StudentController(user);
        initUI();
    }

    private void initUI() {
        setTitle("学生主界面 - " + currentUser.getRealName());
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 顶部菜单栏
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("菜单");

        JMenuItem eventItem = new JMenuItem("活动报名");
        eventItem.addActionListener(e -> studentController.showEnrollmentView());

        JMenuItem profileItem = new JMenuItem("个人信息");
        profileItem.addActionListener(e -> new UserProfileFrame(currentUser).setVisible(true));

        JMenuItem logoutItem = new JMenuItem("退出登录");
        logoutItem.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });

        menu.add(eventItem);
        menu.add(profileItem);
        menu.addSeparator();
        menu.add(logoutItem);
        menuBar.add(menu);
        setJMenuBar(menuBar);

        // 欢迎面板
        JPanel welcomePanel = new JPanel();
        welcomePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        welcomePanel.setLayout(new BoxLayout(welcomePanel, BoxLayout.Y_AXIS));

        JLabel welcomeLabel = new JLabel("欢迎, " + currentUser.getRealName() + "!");
        welcomeLabel.setFont(new Font("宋体", Font.BOLD, 24));
        welcomeLabel.setAlignmentX(CENTER_ALIGNMENT);

        JLabel idLabel = new JLabel("学号: " + currentUser.getUsername());
        idLabel.setFont(new Font("宋体", Font.PLAIN, 16));
        idLabel.setAlignmentX(CENTER_ALIGNMENT);

        JLabel collegeLabel = new JLabel("学院: " + getCollegeName());
        collegeLabel.setFont(new Font("宋体", Font.PLAIN, 16));
        collegeLabel.setAlignmentX(CENTER_ALIGNMENT);

        welcomePanel.add(welcomeLabel);
        welcomePanel.add(Box.createVerticalStrut(10));
        welcomePanel.add(idLabel);
        welcomePanel.add(Box.createVerticalStrut(5));
        welcomePanel.add(collegeLabel);

        // 功能按钮面板
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 3, 20, 0));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(40, 50, 40, 50));

        // 修复：使用正确的studentController变量
        JButton enrollButton = createFeatureButton("活动报名", "报名参加各类活动");
        enrollButton.addActionListener(e -> studentController.showEnrollmentView());

        JButton myEnrollmentsButton = createFeatureButton("我的报名", "查看已报名活动");
        myEnrollmentsButton.addActionListener(e -> studentController.showMyEnrollments());

        JButton profileButton = createFeatureButton("个人信息", "修改个人资料");
        profileButton.addActionListener(e -> new UserProfileFrame(currentUser).setVisible(true));

        buttonPanel.add(enrollButton);
        buttonPanel.add(myEnrollmentsButton);
        buttonPanel.add(profileButton);

        // 状态栏
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        statusPanel.setBorder(BorderFactory.createEtchedBorder());

        // 修复：使用studentController获取报名数量
        int enrolledCount = studentController.getEnrollmentsCount();
        statusLabel = new JLabel("已报名活动: " + enrolledCount);
        statusPanel.add(statusLabel);

        // 添加刷新按钮
        JButton refreshStatusButton = new JButton("刷新");
        refreshStatusButton.addActionListener(e -> {
            int updatedCount = studentController.getEnrollmentsCount();
            statusLabel.setText("已报名活动: " + updatedCount);
        });
        statusPanel.add(refreshStatusButton);

        add(welcomePanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
        add(statusPanel, BorderLayout.SOUTH);
    }

    private String getCollegeName() {
        try {
            return DatabaseUtil.fetchCollegeName(currentUser.getCollegeId());
        } catch (Exception e) {
            return "未知学院";
        }
    }

    private JButton createFeatureButton(String title, String description) {
        JButton button = new JButton();
        button.setLayout(new BorderLayout());
        button.setPreferredSize(new Dimension(200, 120));

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("宋体", Font.BOLD, 18));

        JLabel descLabel = new JLabel(description, SwingConstants.CENTER);
        descLabel.setFont(new Font("宋体", Font.PLAIN, 12));

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(5));
        contentPanel.add(descLabel);

        button.add(contentPanel, BorderLayout.CENTER);
        return button;
    }
}