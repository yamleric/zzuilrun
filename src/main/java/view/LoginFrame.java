package view;

import dao.UserDao;
import dao.UserDaoImpl;
import model.User;
import service.UserService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private UserService userService = new UserService();

    public LoginFrame() {
        setTitle("运动会管理系统 - 登录");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initUI();
    }

    private void initUI() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel titleLabel = new JLabel("运动会管理系统登录", JLabel.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 18));
        panel.add(titleLabel, gbc);

        // 用户名标签和输入框
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        panel.add(new JLabel("用户名:"), gbc);

        gbc.gridx = 1;
        usernameField = new JTextField(15);
        panel.add(usernameField, gbc);

        // 密码标签和输入框
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("密码:"), gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField(15);
        panel.add(passwordField, gbc);
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        // 登录按钮
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.CENTER;
        JButton loginButton = new JButton("登录");
        loginButton.addActionListener(this::handleLogin);

//        loginButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                String username = usernameField.getText();
//                String password = new String(passwordField.getPassword());
//
//                // 这里应该调用业务逻辑验证用户
//                if (username.isEmpty() || password.isEmpty()) {
//                    JOptionPane.showMessageDialog(LoginFrame.this,
//                            "用户名和密码不能为空", "错误", JOptionPane.ERROR_MESSAGE);
//                } else {
//                    // 验证成功，进入主界面
////                     new MainFrame().setVisible(true);
//                     dispose();
//                    JOptionPane.showMessageDialog(LoginFrame.this,
//                            "登录成功", "成功", JOptionPane.INFORMATION_MESSAGE);
//                }
//            }
//        });
        panel.add(loginButton, gbc);

        // 注册按钮
        JButton registerButton = new JButton("注册");
        registerButton.addActionListener(e -> {
            new RegisterFrame().setVisible(true);
        });
        buttonPanel.add(registerButton);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);
        add(panel);
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
            JOptionPane.showMessageDialog(this,
                    "登录成功", "成功", JOptionPane.INFORMATION_MESSAGE);
            dispose();

            // 根据用户角色打开不同界面
            if (user.getRole() == 1) { // 普通用户
//                new UserMainFrame(user).setVisible(true);
            } else { // 管理员
//                new AdminMainFrame(user).setVisible(true);
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "用户名或密码错误", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openMainWindow(User user) {
        if (user.getRole() == 2) {
//            new AdminMainFrame(user).setVisible(true);
        } else {
//            new UserMainFrame(user).setVisible(true);
        }
    }
}

//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(() -> {
//            LoginFrame frame = new LoginFrame();
//            frame.setVisible(true);
//        });
//    }
//}