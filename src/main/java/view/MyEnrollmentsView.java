// view/MyEnrollmentsView.java
package view;

import controller.StudentController;
import model.Enrollment;
import model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MyEnrollmentsView extends JFrame {
    private final StudentController controller;
    private final User currentUser;
    private JTable enrollmentsTable;

    public MyEnrollmentsView(StudentController controller, User user) {
        this.controller = controller;
        this.currentUser = user;
        initUI();
        loadEnrollments();
    }

    private void initUI() {
        setTitle("我的报名 - " + currentUser.getRealName());
        setSize(800, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // 主面板
        JPanel mainPanel = new JPanel(new BorderLayout());

        // 表格模型
        String[] columnNames = {"报名ID", "活动名称", "报名时间", "状态", "操作"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4; // 只有操作列可编辑
            }
        };

        enrollmentsTable = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(enrollmentsTable);

        // 操作按钮面板
        JPanel buttonPanel = new JPanel();
        JButton cancelButton = new JButton("取消报名");
        JButton refreshButton = new JButton("刷新");
        JButton backButton = new JButton("返回主界面");

        cancelButton.addActionListener(e -> cancelEnrollment());
        refreshButton.addActionListener(e -> loadEnrollments());
        backButton.addActionListener(e -> dispose());

        buttonPanel.add(cancelButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(backButton);

        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void loadEnrollments() {
        DefaultTableModel model = (DefaultTableModel) enrollmentsTable.getModel();
        model.setRowCount(0); // 清除现有数据

        List<Enrollment> enrollments = controller.getUserEnrollments();
        if (enrollments.isEmpty()) {
            JOptionPane.showMessageDialog(this, "您尚未报名任何活动", "信息", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        for (Enrollment enrollment : enrollments) {
            String enrollTime = enrollment.getEnrollTime() != null ?
                    enrollment.getEnrollTime().format(formatter) : "未知时间";

            Object[] row = {
                    enrollment.getEnrollmentId(),
                    enrollment.getEventName(),
                    enrollTime,
                    enrollment.getStatusName(), // 使用Enrollment的getStatusName
                    "取消"
            };
            model.addRow(row);
        }
    }

    private void cancelEnrollment() {
        int selectedRow = enrollmentsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请选择要取消的报名", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int enrollmentId = (int) enrollmentsTable.getValueAt(selectedRow, 0);
        if (controller.cancelEnrollment(enrollmentId)) {
            JOptionPane.showMessageDialog(this, "报名已取消", "成功", JOptionPane.INFORMATION_MESSAGE);
            loadEnrollments(); // 刷新列表
        } else {
            JOptionPane.showMessageDialog(this, "取消报名失败，请联系管理员", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
}