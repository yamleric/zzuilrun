package view;

import controller.AdminController;
import model.User;
import util.DatabaseUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class UserManagementPanel extends JPanel {
    private final AdminController adminController;
    private JTable userTable;
    private DefaultTableModel tableModel;

    public UserManagementPanel(AdminController controller) {
        this.adminController = controller;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        initUI();
        loadUserData();
    }

    private void initUI() {
        // 标题
        JLabel titleLabel = new JLabel("用户管理", SwingConstants.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        // 表格
        tableModel = new DefaultTableModel(new String[]{"ID", "用户名", "真实姓名", "学院", "角色", "状态"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 使表格不可编辑
            }
        };
        userTable = new JTable(tableModel);
        userTable.setRowHeight(30);
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userTable.getTableHeader().setReorderingAllowed(false); // 不可移动列

        JScrollPane scrollPane = new JScrollPane(userTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("用户列表"));
        add(scrollPane, BorderLayout.CENTER);

        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JButton addButton = new JButton("添加用户");
        JButton editButton = new JButton("编辑用户");
        JButton deleteButton = new JButton("删除用户");
        JButton toggleStatusButton = new JButton("启用/禁用");
        JButton refreshButton = new JButton("刷新");

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(toggleStatusButton);
        buttonPanel.add(refreshButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // 按钮事件
        addButton.addActionListener(e -> showAddUserDialog());
        editButton.addActionListener(e -> showEditUserDialog());
        deleteButton.addActionListener(e -> deleteSelectedUser());
        toggleStatusButton.addActionListener(e -> toggleUserStatus());
        refreshButton.addActionListener(e -> loadUserData());
    }

    private void loadUserData() {
        List<User> users = adminController.getAllUsers();
        tableModel.setRowCount(0); // 清空表格

        for (User user : users) {
            Object[] rowData = new Object[] {
                    user.getUserId(),
                    user.getUsername(),
                    user.getRealName(),
                    DatabaseUtil.fetchCollegeName(user.getCollegeId()),
                    user.getRoleName(),
                    user.getStatus() == 1 ? "启用" : "禁用"
            };
            tableModel.addRow(rowData);
        }
    }

    private User getSelectedUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow < 0) return null;

        int userId = (int) tableModel.getValueAt(selectedRow, 0);
        return adminController.getUserById(userId);
    }

    private void showAddUserDialog() {
        UserDialog dialog = new UserDialog(null, adminController, null);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            loadUserData(); // 刷新用户列表
        }
    }

    private void showEditUserDialog() {
        User selectedUser = getSelectedUser();
        if (selectedUser == null) {
            JOptionPane.showMessageDialog(this, "请先选择一个用户", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        UserDialog dialog = new UserDialog(null, adminController, selectedUser);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            loadUserData(); // 刷新用户列表
        }
    }

    private void deleteSelectedUser() {
        User selectedUser = getSelectedUser();
        if (selectedUser == null) {
            JOptionPane.showMessageDialog(this, "请先选择一个用户", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "确定要删除用户 " + selectedUser.getRealName() + "(" + selectedUser.getUsername() + ")?",
                "确认删除",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = adminController.deleteUser(selectedUser.getUserId());
            if (success) {
                JOptionPane.showMessageDialog(this, "用户删除成功", "成功", JOptionPane.INFORMATION_MESSAGE);
                loadUserData();
            } else {
                JOptionPane.showMessageDialog(this, "删除用户失败", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void toggleUserStatus() {
        User selectedUser = getSelectedUser();
        if (selectedUser == null) {
            JOptionPane.showMessageDialog(this, "请先选择一个用户", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int newStatus = selectedUser.getStatus() == 1 ? 0 : 1; // 切换状态
        boolean success = adminController.changeUserStatus(selectedUser.getUserId(), newStatus);

        if (success) {
            JOptionPane.showMessageDialog(this, "用户状态已更新", "成功", JOptionPane.INFORMATION_MESSAGE);
            loadUserData();
        } else {
            JOptionPane.showMessageDialog(this, "更新状态失败", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
}