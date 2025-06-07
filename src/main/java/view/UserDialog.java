package view;

import controller.AdminController;
import model.User;
import util.DatabaseUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

public class UserDialog extends JDialog {
    private final AdminController adminController;
    private final User user;
    private boolean confirmed = false;

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField realNameField;
    private JComboBox<String> collegeComboBox;
    private JComboBox<String> roleComboBox;
    private JTextField phoneField;
    private JTextField emailField;
    private JComboBox<String> statusComboBox;

    public UserDialog(JFrame parent, AdminController controller, User user) {
        super(parent, user == null ? "添加用户" : "编辑用户", true);
        this.adminController = controller;
        this.user = user;

        setSize(500, 400);
        setLocationRelativeTo(parent);
        initUI();
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new GridLayout(8, 2, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 用户名
        mainPanel.add(new JLabel("用户名:"));
        usernameField = new JTextField();
        if (user != null) {
            usernameField.setText(user.getUsername());
            usernameField.setEnabled(false); // 编辑时不可修改用户名
        }
        mainPanel.add(usernameField);

        // 密码
        mainPanel.add(new JLabel("密码:"));
        passwordField = new JPasswordField();
        if (user == null) {
            mainPanel.add(passwordField);
        } else {
            JPanel passwordPanel = new JPanel(new BorderLayout());
            passwordPanel.add(passwordField, BorderLayout.CENTER);
            JLabel hint = new JLabel("(留空不修改)");
            hint.setFont(hint.getFont().deriveFont(Font.ITALIC, 10));
            passwordPanel.add(hint, BorderLayout.EAST);
            mainPanel.add(passwordPanel);
        }

        // 真实姓名
        mainPanel.add(new JLabel("真实姓名:"));
        realNameField = new JTextField(user != null ? user.getRealName() : "");
        mainPanel.add(realNameField);

        // 学院
        mainPanel.add(new JLabel("学院:"));
        collegeComboBox = new JComboBox<>(new String[]{"计算机学院", "电气学院", "机械学院", "软件学院"});
        if (user != null) {
            collegeComboBox.setSelectedItem(DatabaseUtil.fetchCollegeName(user.getCollegeId()));
        }
        mainPanel.add(collegeComboBox);

        // 角色
        mainPanel.add(new JLabel("角色:"));
        roleComboBox = new JComboBox<>(new String[]{"普通用户", "管理员"});
        if (user != null) {
            roleComboBox.setSelectedIndex(user.getRole() - 1); // 1:普通用户, 2:管理员
        }
        mainPanel.add(roleComboBox);

        // 手机
        mainPanel.add(new JLabel("手机:"));
        phoneField = new JTextField(user != null ? user.getPhone() : "");
        mainPanel.add(phoneField);

        // 邮箱
        mainPanel.add(new JLabel("邮箱:"));
        emailField = new JTextField(user != null ? user.getEmail() : "");
        mainPanel.add(emailField);

        // 状态
        mainPanel.add(new JLabel("状态:"));
        statusComboBox = new JComboBox<>(new String[]{"禁用", "启用"});
        if (user != null) {
            statusComboBox.setSelectedIndex(user.getStatus());
        }
        mainPanel.add(statusComboBox);

        // 按钮
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton okButton = new JButton("确定");
        JButton cancelButton = new JButton("取消");
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        // 事件
        okButton.addActionListener(e -> saveUser());
        cancelButton.addActionListener(e -> dispose());

        getContentPane().add(mainPanel, BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);
    }

    private void saveUser() {
        // 验证必填字段
        if (usernameField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "用户名不能为空", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (user == null && passwordField.getPassword().length == 0) { // 添加时密码不能为空
            JOptionPane.showMessageDialog(this, "密码不能为空", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        User userToSave = user != null ? user : new User();
        userToSave.setUsername(usernameField.getText());

        // 设置密码（编辑时留空表示不修改）
        if (user == null || passwordField.getPassword().length > 0) {
            userToSave.setPassword(new String(passwordField.getPassword()));
        }

        userToSave.setRealName(realNameField.getText());
        userToSave.setCollegeId(DatabaseUtil.getCollegeIdByName((String) collegeComboBox.getSelectedItem()));
        userToSave.setRole(roleComboBox.getSelectedIndex() + 1); // 1 or 2
        userToSave.setPhone(phoneField.getText());
        userToSave.setEmail(emailField.getText());
        userToSave.setStatus(statusComboBox.getSelectedIndex());

        boolean success;
        if (user == null) {
            success = adminController.addUser(userToSave);
        } else {
            success = adminController.updateUser(userToSave);
        }

        if (success) {
            confirmed = true;
            JOptionPane.showMessageDialog(this, "用户保存成功", "成功", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "保存用户失败", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isConfirmed() {
        return confirmed;
    }
}