package view;

import model.User;
import service.CollegeService;
import service.UserService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.List;

public class RegisterFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField realNameField;
    private JComboBox<String> genderComboBox;
    private JComboBox<String> collegeComboBox;
    private JTextField phoneField;
    private JTextField emailField;
    private JLabel validationLabel;

    private final UserService userService = new UserService();
    private final CollegeService collegeService = new CollegeService();

    public RegisterFrame() {
        setTitle("用户注册");
        setSize(500, 450);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setIconImage(new ImageIcon(getClass().getResource("/icons/register_icon.png")).getImage());

        initUI();
        loadCollegesFromDB();
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        // 标题标签
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel titleLabel = new JLabel("新用户注册");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 18));
        mainPanel.add(titleLabel, gbc);

        // 学号/工号
        gbc.gridwidth = 1;
        gbc.gridy++;
        mainPanel.add(new JLabel("学号/工号:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        usernameField = new JTextField();
        usernameField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        mainPanel.add(usernameField, gbc);

        // 密码
        gbc.gridx = 0;
        gbc.gridy++;
        mainPanel.add(new JLabel("密码:"), gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField();
        passwordField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        mainPanel.add(passwordField, gbc);

        // 真实姓名
        gbc.gridx = 0;
        gbc.gridy++;
        mainPanel.add(new JLabel("真实姓名:"), gbc);

        gbc.gridx = 1;
        realNameField = new JTextField();
        realNameField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        mainPanel.add(realNameField, gbc);

        // 性别
        gbc.gridx = 0;
        gbc.gridy++;
        mainPanel.add(new JLabel("性别:"), gbc);

        gbc.gridx = 1;
        genderComboBox = new JComboBox<>(new String[]{"男", "女"});
        genderComboBox.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        mainPanel.add(genderComboBox, gbc);

        // 院系
        gbc.gridx = 0;
        gbc.gridy++;
        mainPanel.add(new JLabel("院系:"), gbc);

        gbc.gridx = 1;
        collegeComboBox = new JComboBox<>();
        collegeComboBox.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        mainPanel.add(collegeComboBox, gbc);

        // 电话
        gbc.gridx = 0;
        gbc.gridy++;
        mainPanel.add(new JLabel("电话:"), gbc);

        gbc.gridx = 1;
        phoneField = new JTextField();
        phoneField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        mainPanel.add(phoneField, gbc);

        // 邮箱
        gbc.gridx = 0;
        gbc.gridy++;
        mainPanel.add(new JLabel("邮箱:"), gbc);

        gbc.gridx = 1;
        emailField = new JTextField();
        emailField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        mainPanel.add(emailField, gbc);

        // 验证提示
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        validationLabel = new JLabel(" ");
        validationLabel.setForeground(Color.RED);
        validationLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        mainPanel.add(validationLabel, gbc);

        // 按钮面板
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0));

        JButton registerButton = new JButton("注册");
        registerButton.setBackground(new Color(70, 130, 180));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFont(new Font("微软雅黑", Font.BOLD, 14));
        registerButton.addActionListener(this::handleRegister);

        JButton cancelButton = new JButton("取消");
        cancelButton.setBackground(new Color(220, 220, 220));
        cancelButton.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(registerButton);
        buttonPanel.add(cancelButton);
        mainPanel.add(buttonPanel, gbc);

        // 添加实时输入验证
        addInputValidation();

        add(mainPanel);
    }

    private void addInputValidation() {
        // 学号格式验证（12位数字）
        usernameField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { validateInput(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { validateInput(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { validateInput(); }
        });

        // 密码强度验证
        passwordField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { validateInput(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { validateInput(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { validateInput(); }
        });
    }

    private void validateInput() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        StringBuilder errors = new StringBuilder();

        // 学号验证
        if (username.isEmpty()) {
            errors.append("学号不能为空。 ");
        } else if (!username.matches("^\\d{12}$")) {
            errors.append("学号必须是12位数字。 ");
        }

        // 密码验证
        if (password.isEmpty()) {
            errors.append("密码不能为空。 ");
        } else if (password.length() < 8) {
            errors.append("密码至少需要8位字符。 ");
        } else if (!password.matches(".*[a-zA-Z]+.*")) {
            errors.append("密码需包含字母。 ");
        } else if (!password.matches(".*\\d+.*")) {
            errors.append("密码需包含数字。 ");
        }

        validationLabel.setText(errors.toString().trim());
    }

    private void loadCollegesFromDB() {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            private List<String> colleges;

            @Override
            protected Void doInBackground() {
                try {
                    colleges = collegeService.getAllColleges();
                    if (colleges == null || colleges.isEmpty()) {
                        colleges = Arrays.asList("计算机学院", "软件学院", "电气学院", "机械学院");
                    }
                } catch (Exception e) {
                    colleges = Arrays.asList("院系加载失败");
                }
                return null;
            }

            @Override
            protected void done() {
                if (colleges != null) {
                    collegeComboBox.removeAllItems();
                    for (String college : colleges) {
                        collegeComboBox.addItem(college);
                    }
                }
            }
        };
        worker.execute();
    }

    private void handleRegister(ActionEvent e) {
        // 获取用户输入
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String realName = realNameField.getText().trim();
        String gender = (String) genderComboBox.getSelectedItem();
        String collegeName = (String) collegeComboBox.getSelectedItem();
        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim();

        // 基本输入验证
        if (!validateInputFields(username, password, email)) {
            return;
        }

        // 创建用户对象
        User user = createUser(username, password, realName, gender, collegeName, phone, email);

        // 创建加载对话框（使用final修饰确保线程安全）
        final JDialog loadingDialog = new JDialog(this, "", true);
        loadingDialog.setUndecorated(true);
        loadingDialog.setSize(100, 100);
        loadingDialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        JLabel label = new JLabel("处理中...", SwingConstants.CENTER);
        label.setIcon(new ImageIcon(getClass().getResource("/icons/loading.gif")));
        panel.add(label, BorderLayout.CENTER);
        loadingDialog.add(panel);

        loadingDialog.setVisible(true);

        // 在后台线程中执行注册操作
        new Thread(() -> {
            boolean success = false;
            String errorMessage = "";

            try {
                success = userService.register(user);
            } catch (Exception ex) {
                errorMessage = ex.getMessage();
            } finally {
                // 确保UI更新在主线程执行
                boolean finalSuccess = success;
                String finalErrorMessage = errorMessage;
                SwingUtilities.invokeLater(() -> {
                    loadingDialog.dispose();

                    if (finalSuccess) {
                        showSuccessMessage();
                    } else {
                        if (finalErrorMessage.isEmpty()) {
                            validationLabel.setText("注册失败：用户名可能已存在");
                        } else {
                            validationLabel.setText("注册失败: " + finalErrorMessage);
                        }
                    }
                });
            }
        }).start();
    }

    private boolean validateInputFields(String username, String password, String email) {
        // 检查必填字段
        if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
            validationLabel.setText("学号、密码和邮箱是必填项");
            return false;
        }

        // 学号格式验证
        if (!username.matches("^\\d{12}$")) {
            validationLabel.setText("学号必须是12位数字");
            return false;
        }

        // 密码强度验证
        if (password.length() < 8) {
            validationLabel.setText("密码至少需要8位字符");
            return false;
        }

        if (!password.matches(".*[a-zA-Z]+.*")) {
            validationLabel.setText("密码需包含字母");
            return false;
        }

        if (!password.matches(".*\\d+.*")) {
            validationLabel.setText("密码需包含数字");
            return false;
        }

        // 邮箱格式验证
        if (!email.matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            validationLabel.setText("邮箱格式不正确");
            return false;
        }

        return true;
    }

    private User createUser(String username, String password, String realName,
                            String gender, String collegeName, String phone, String email) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setRealName(realName);
        user.setGender(gender);
        user.setCollegeId(collegeService.getCollegeIdByName(collegeName));
        user.setPhone(phone);
        user.setEmail(email);
        user.setRole(1);
        user.setStatus(1);
        return user;
    }

    private void showSuccessMessage() {
        JOptionPane.showMessageDialog(
                this,
                "注册成功！请使用学号登录系统。",
                "注册成功",
                JOptionPane.INFORMATION_MESSAGE
        );
        dispose();
    }
}