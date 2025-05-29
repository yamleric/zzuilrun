package view;

import model.User;
import service.UserService;
import javax.swing.*;
import java.awt.*;

public class UserProfileFrame extends JFrame {
    private final User currentUser;
    private final JTextField txtPhone = new JTextField(15);
    private final JTextField txtEmail = new JTextField(15);

    public UserProfileFrame(User user) {
        this.currentUser = user;

        JPanel panel = new JPanel(new GridLayout(3, 2));
        panel.add(new JLabel("电话:"));
        panel.add(txtPhone);
        panel.add(new JLabel("邮箱:"));
        panel.add(txtEmail);

        JButton btnSave = new JButton("保存修改");
        btnSave.addActionListener(e -> saveChanges());
        panel.add(btnSave);

        loadUserData();
        add(panel);
        setSize(600, 300);
        setLocationRelativeTo(null);
    }

    private void loadUserData() {
        txtPhone.setText(currentUser.getPhone());
        txtEmail.setText(currentUser.getEmail());
    }

    private void saveChanges() {
        currentUser.setPhone(txtPhone.getText());
        currentUser.setEmail(txtEmail.getText());

        if (new UserService().updateUser(currentUser)) {
            JOptionPane.showMessageDialog(this, "修改已保存");
        }
    }
}