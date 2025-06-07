// 新建面板：EnrollmentStatsPanel.java
package view;

import controller.AdminController;
import model.EnrollmentStats;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class EnrollmentStatsPanel extends JPanel {
    private final AdminController adminController;
    private JTable eventTable;
    private JTable collegeTable;

    public EnrollmentStatsPanel(AdminController adminController) {
        this.adminController = adminController;
        setLayout(new BorderLayout());
        initUI();
        loadData();
    }

    private void initUI() {
        // 标题面板
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel titleLabel = new JLabel("运动会报名统计");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 20));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);

        // 选项卡面板
        JTabbedPane tabbedPane = new JTabbedPane();

        // 按项目统计
        JScrollPane eventScrollPane = createEventStatsPanel();
        tabbedPane.addTab("项目报名情况", eventScrollPane);

        // 按学院统计
        JScrollPane collegeScrollPane = createCollegeStatsPanel();
        tabbedPane.addTab("学院报名情况", collegeScrollPane);

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JScrollPane createEventStatsPanel() {
        String[] columnNames = {"项目名称", "报名总数", "已通过", "待审核", "已拒绝"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        eventTable = new JTable(model);
        eventTable.setAutoCreateRowSorter(true);
        eventTable.getTableHeader().setReorderingAllowed(false);

        return new JScrollPane(eventTable);
    }

    private JScrollPane createCollegeStatsPanel() {
        String[] columnNames = {"学院名称", "报名总数", "已通过", "待审核", "已拒绝"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        collegeTable = new JTable(model);
        collegeTable.setAutoCreateRowSorter(true);
        collegeTable.getTableHeader().setReorderingAllowed(false);

        return new JScrollPane(collegeTable);
    }

    private void loadData() {
        loadEventStats();
        loadCollegeStats();
    }

    private void loadEventStats() {
        DefaultTableModel model = (DefaultTableModel) eventTable.getModel();
        model.setRowCount(0); // 清除现有数据

        List<EnrollmentStats> eventStats = adminController.getEventEnrollmentStats();

        for (EnrollmentStats stats : eventStats) {
            model.addRow(new Object[]{
                    stats.getEventName(),
                    stats.getTotalRegistrations(),
                    stats.getApprovedCount(),
                    stats.getPendingCount(),
                    stats.getRejectedCount()
            });
        }
    }

    private void loadCollegeStats() {
        DefaultTableModel model = (DefaultTableModel) collegeTable.getModel();
        model.setRowCount(0); // 清除现有数据

        List<EnrollmentStats> collegeStats = adminController.getCollegeEnrollmentStats();

        for (EnrollmentStats stats : collegeStats) {
            model.addRow(new Object[]{
                    stats.getCollegeName(),
                    stats.getTotalRegistrations(),
                    stats.getApprovedCount(),
                    stats.getPendingCount(),
                    stats.getRejectedCount()
            });
        }
    }
}