package view;

import dao.UserDao;
import dao.UserDaoImpl;
import model.User;
import service.UserService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private UserService userService = new UserService();

    public LoginFrame() {
        setTitle("运动会管理系统 - 登录");
        setSize(900, 700); // 增加高度以适应大图标
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setUndecorated(true); // 移除默认边框

        initUI();
    }

    private void initUI() {
        // 创建主面板
        JPanel mainPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;

                // 渐变背景
                GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(64, 115, 225),
                        getWidth(), getHeight(), new Color(34, 85, 185));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                // 绘制装饰性元素
                g2d.setColor(new Color(255, 255, 255, 50));
                for (int i = 0; i < 10; i++) {
                    int size = 60 + i * 20;
                    g2d.fillOval(50 + i * 30, 50 + i * 10, size, size);
                }
            }
        };
        mainPanel.setOpaque(false);

        // 创建登录表单面板
        JPanel loginPanel = createLoginPanel();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(20, 20, 20, 20);

        mainPanel.add(loginPanel, gbc);

        // 添加关闭按钮
        JButton closeButton = createCloseButton();
        gbc.gridy = 1;
        mainPanel.add(closeButton, gbc);

        // 设置窗体圆角
        setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 25, 25));
        add(mainPanel);
    }

    private JPanel createLoginPanel() {
        // 创建登录面板
        JPanel loginPanel = new JPanel(new GridBagLayout());
        loginPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        loginPanel.setBackground(new Color(255, 255, 255, 240));
        loginPanel.setOpaque(true);

        // 设置阴影效果
        loginPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 230, 240), 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 10, 15, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        // 添加标题
        JLabel titleLabel = new JLabel("欢迎登录", JLabel.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 32));
        titleLabel.setForeground(new Color(64, 115, 225));
        loginPanel.add(titleLabel, gbc);

        // 添加副标题
        gbc.gridy = 1;
        JLabel subTitle = new JLabel("郑州轻工业大学运动会管理系统", JLabel.CENTER);
        subTitle.setFont(new Font("微软雅黑", Font.PLAIN, 18));
        subTitle.setForeground(new Color(150, 150, 150));
        loginPanel.add(subTitle, gbc);

        // 添加大图标 - 使用缩放功能
        gbc.gridy = 2;
        gbc.insets = new Insets(20, 10, 20, 10); // 减少上下边距
        ImageIcon originalIcon = new ImageIcon(getClass().getResource("/images/郑州轻工业大学-logo-512px.png"));
        Image scaledImage = originalIcon.getImage().getScaledInstance(128, 128, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);
        JLabel iconLabel = new JLabel(scaledIcon);
        loginPanel.add(iconLabel, gbc);

        // 添加用户名标签
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(5, 10, 5, 10); // 减少边距
        JLabel usernameLabel = new JLabel("用户名:");
        usernameLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        loginPanel.add(usernameLabel, gbc);

        // 添加用户名输入框
        gbc.gridy = 4;
        gbc.insets = new Insets(0, 10, 10, 10); // 减少边距
        usernameField = new JTextField(20);
        styleTextField(usernameField);
        loginPanel.add(usernameField, gbc);

        // 添加密码标签
        gbc.gridy = 5;
        gbc.insets = new Insets(5, 10, 5, 10); // 减少边距
        JLabel passwordLabel = new JLabel("密码:");
        passwordLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        loginPanel.add(passwordLabel, gbc);

        // 添加密码输入框
        gbc.gridy = 6;
        gbc.insets = new Insets(0, 10, 15, 10); // 减少边距
        passwordField = new JPasswordField(20);
        styleTextField(passwordField);
        loginPanel.add(passwordField, gbc);

        // 添加登录按钮
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 10, 5, 10); // 减少边距
        JButton loginButton = createStyledButton("登 录", new Color(64, 115, 225));
        loginButton.addActionListener(this::handleLogin);
        loginButton.setPreferredSize(new Dimension(250, 40)); // 减小按钮高度
        loginPanel.add(loginButton, gbc);

        // 添加注册按钮
        gbc.gridy = 8;
        gbc.insets = new Insets(5, 10, 5, 10); // 减少边距
        JButton registerButton = createStyledButton("注 册", new Color(100, 150, 240));
        registerButton.addActionListener(e -> new RegisterFrame().setVisible(true));
        registerButton.setPreferredSize(new Dimension(250, 35)); // 减小按钮高度
        loginPanel.add(registerButton, gbc);

        // 添加忘记密码链接
        gbc.gridy = 9;
        gbc.insets = new Insets(5, 10, 5, 10); // 减少边距
        JLabel forgotLabel = new JLabel("<html><a href='#'>忘记密码?</a></html>", JLabel.CENTER);
        forgotLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        forgotLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                JOptionPane.showMessageDialog(LoginFrame.this,
                        "请联系系统管理员重置密码", "忘记密码", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        forgotLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        loginPanel.add(forgotLabel, gbc);

        return loginPanel;
    }

    private void styleTextField(JTextField field) {
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 230, 240), 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12) // 减小内边距
        ));
        field.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        field.setOpaque(false);
    }

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // 绘制按钮背景
                GradientPaint gradient = new GradientPaint(
                        0, 0, color.brighter(),
                        0, getHeight(), color.darker());
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

                // 绘制按钮文字
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;

                g2d.setColor(Color.WHITE);
                g2d.drawString(getText(), x, y);
            }
        };

        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setFont(new Font("微软雅黑", Font.BOLD, 14)); // 减小字体大小
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        return button;
    }

    private JButton createCloseButton() {
        JButton closeButton = new JButton("退出系统");
        closeButton.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        closeButton.setForeground(Color.WHITE);
        closeButton.setContentAreaFilled(false);
        closeButton.setBorderPainted(false);
        closeButton.setFocusPainted(false);
        closeButton.addActionListener(e -> System.exit(0));
        closeButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        return closeButton;
    }

    private void handleLogin(ActionEvent e) {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "用户名和密码不能为空", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        User user = userService.login(username, password);
        if (user != null) {
            // 登录成功动画效果
            new Thread(() -> {
                for (float opacity = 1.0f; opacity > 0; opacity -= 0.05f) {
                    final float op = opacity;
                    SwingUtilities.invokeLater(() -> {
                        setOpacity(op);
                    });
                    try {
                        Thread.sleep(30);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
                dispose();
            }).start();

            // 根据用户角色打开不同界面
            if (user.getRole() == 1) { // 普通用户
                new UserMainFrame(user).setVisible(true);
            } else { // 管理员
                new AdminMainFrame(user).setVisible(true);
            }
        } else {
            // 登录失败动画效果
            JOptionPane.showMessageDialog(this,
                    "用户名或密码错误", "错误", JOptionPane.ERROR_MESSAGE);

            // 密码输入框抖动效果
            new Thread(() -> {
                Point original = passwordField.getLocation();
                for (int i = 0; i < 5; i++) {
                    int offset = (i % 2 == 0) ? 10 : -10;
                    passwordField.setLocation(original.x + offset, original.y);
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
                passwordField.setLocation(original);
                passwordField.setText("");
            }).start();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginFrame frame = new LoginFrame();
            frame.setVisible(true);
        });
    }
}