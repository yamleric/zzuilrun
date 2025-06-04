package view;

import controller.AdminController;

import javax.swing.*;
import java.awt.*;

public class UserManagementPanel extends JPanel {
    private final AdminController adminController;

    public UserManagementPanel(AdminController adminController) {
        this.adminController = adminController;

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        initUI();
    }

    private void initUI() {
        JLabel titleLabel = new JLabel("用户管理功能开发中", SwingConstants.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));
        add(titleLabel, BorderLayout.CENTER);
    }
}