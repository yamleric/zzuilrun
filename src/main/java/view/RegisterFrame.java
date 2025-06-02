package view;

import model.User;
import service.UserService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class RegisterFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField realNameField;
    private JComboBox<String> genderComboBox;
    private JComboBox<String> collegeComboBox;
    private JTextField phoneField;
    private JTextField emailField;

    private UserService userService = new UserService();

    public RegisterFrame() {
        setTitle("用户注册");
        setSize(400, 350);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        initUI();
    }

    private void initUI() {
        JPanel panel = new JPanel(new GridLayout(8, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 用户名
        panel.add(new JLabel("学号/工号:"));
        usernameField = new JTextField();
        panel.add(usernameField);

        // 密码
        panel.add(new JLabel("密码:"));
        passwordField = new JPasswordField();
        panel.add(passwordField);

        // 真实姓名
        panel.add(new JLabel("真实姓名:"));
        realNameField = new JTextField();
        panel.add(realNameField);

        // 性别
        panel.add(new JLabel("性别:"));
        genderComboBox = new JComboBox<>(new String[]{"男", "女"});
        panel.add(genderComboBox);

        // 院系
        panel.add(new JLabel("院系:"));
        collegeComboBox = new JComboBox<>(new String[]{"计算机学院","软件学院", "电气学院", "机械学院"});
        panel.add(collegeComboBox);

        // 电话
        panel.add(new JLabel("电话:"));
        phoneField = new JTextField();
        panel.add(phoneField);

        // 邮箱
        panel.add(new JLabel("邮箱:"));
        emailField = new JTextField();
        panel.add(emailField);

        // 注册按钮
        JButton registerButton = new JButton("注册");
        registerButton.addActionListener(this::handleRegister);
        panel.add(registerButton);

        // 取消按钮
        JButton cancelButton = new JButton("取消");
        cancelButton.addActionListener(e -> dispose());
        panel.add(cancelButton);

        add(panel);
    }

    private void handleRegister(ActionEvent e) {
        User user = new User();
        user.setUsername(usernameField.getText().trim());
        user.setPassword(new String(passwordField.getPassword()).trim());
        user.setRealName(realNameField.getText().trim());
        user.setGender(genderComboBox.getSelectedItem().toString());
        user.setCollegeId(collegeComboBox.getSelectedIndex() + 1);
        user.setPhone(phoneField.getText().trim());
        user.setEmail(emailField.getText().trim());
        user.setRole(1); // 默认普通用户

        if (userService.register(user)) {
            JOptionPane.showMessageDialog(this,
                    "注册成功", "成功", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                    "注册失败，用户名可能已存在", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
}