package view;

import controller.AdminController;
import model.Enrollment;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ApprovalView extends JPanel {
    private final AdminController controller;
    private JTable enrollmentsTable;
    private DefaultTableModel tableModel;

    public ApprovalView(AdminController controller) {
        this.controller = controller;
        initUI();
        loadEnrollments();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // 标题
        JLabel titleLabel = new JLabel("报名审核", SwingConstants.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        // 表格模型
        String[] columnNames = {"ID", "用户账号", "用户姓名", "活动名称", "报名时间", "状态", "操作"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6; // 只有操作列可编辑
            }
        };

        enrollmentsTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(enrollmentsTable);
        add(scrollPane, BorderLayout.CENTER);

        // 操作按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));

        JButton approveButton = new JButton("批准");
        approveButton.addActionListener(e -> processApproval(true));

        JButton rejectButton = new JButton("拒绝");
        rejectButton.addActionListener(e -> processApproval(false));

        JButton refreshButton = new JButton("刷新");
        refreshButton.addActionListener(e -> loadEnrollments());

        buttonPanel.add(approveButton);
        buttonPanel.add(rejectButton);
        buttonPanel.add(refreshButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadEnrollments() {
        tableModel.setRowCount(0); // 清除现有数据

        List<Enrollment> enrollments = controller.getPendingEnrollments();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        for (Enrollment enrollment : enrollments) {
            String enrollTime = enrollment.getEnrollTime() != null ?
                    enrollment.getEnrollTime().format(formatter) : "未知时间";

            Object[] row = {
                    enrollment.getEnrollmentId(),
                    enrollment.getUsername(),
                    enrollment.getRealName(),
                    enrollment.getEventName(),
                    enrollTime,
                    enrollment.getStatusName(),
                    null // 操作列用按钮填充
            };
            tableModel.addRow(row);
        }

        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this,
                    "没有待审核的报名",
                    "信息",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void processApproval(boolean isApprove) {
        int selectedRow = enrollmentsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请选择要操作的报名", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int enrollmentId = (int) tableModel.getValueAt(selectedRow, 0);
        String studentName = (String) tableModel.getValueAt(selectedRow, 2);
        String eventName = (String) tableModel.getValueAt(selectedRow, 3);

        boolean success;
        if (isApprove) {
            success = controller.approveEnrollment(enrollmentId);
        } else {
            success = controller.rejectEnrollment(enrollmentId);
        }

        if (success) {
            String action = isApprove ? "批准" : "拒绝";
            JOptionPane.showMessageDialog(this,
                    "已成功" + action + " " + studentName + " 报名 " + eventName,
                    "操作成功",
                    JOptionPane.INFORMATION_MESSAGE);
            loadEnrollments(); // 刷新列表
        } else {
            JOptionPane.showMessageDialog(this,
                    "操作失败，请重试",
                    "错误",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}