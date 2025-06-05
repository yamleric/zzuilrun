// view/EnrollmentView.java
package view;

import controller.StudentController;
import model.Event;
import model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class EnrollmentView extends JFrame {
    private final StudentController controller;
    private final User currentUser;
    private JTable eventsTable;

    public EnrollmentView(StudentController controller, User user) {
        this.controller = controller;
        this.currentUser = user;
        initUI();
        loadEvents();
    }

    private void initUI() {
        setTitle("活动报名 - " + currentUser.getRealName());
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // 主面板
        JPanel mainPanel = new JPanel(new BorderLayout());

        // 表格模型
        String[] columnNames = {"ID", "活动名称", "类型", "性别限制", "开始时间", "地点", "操作"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6; // 只有操作列可编辑
            }
        };

        eventsTable = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(eventsTable);

        // 优化表头显示
        JTableHeader header = eventsTable.getTableHeader();
        header.setFont(new Font("宋体", Font.BOLD, 14));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        // 操作按钮面板
        JPanel buttonPanel = new JPanel();
        JButton enrollButton = new JButton("报名");
        JButton refreshButton = new JButton("刷新");
        JButton backButton = new JButton("返回主界面");

        enrollButton.addActionListener(e -> enrollToSelectedEvent());
        refreshButton.addActionListener(e -> loadEvents());
        backButton.addActionListener(e -> dispose());

        buttonPanel.add(enrollButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(backButton);

        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void loadEvents() {
        DefaultTableModel model = (DefaultTableModel) eventsTable.getModel();
        model.setRowCount(0); // 清除现有数据

        List<Event> events = controller.getAvailableEvents();

        // 检查活动列表是否为空
        if (events.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "没有可用的活动",
                    "信息",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // 创建日期时间格式化器
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        for (Event event : events) {
            // 安全处理开始时间
            String startTime = event.getStartTime() != null ?
                    event.getStartTime().format(formatter) : "时间未定";

            // 获取事件类型名称
            String eventType = "未知类型";
            switch(event.getEventType()) {
                case Event.EVENT_TYPE_INDIVIDUAL:
                    eventType = "个人";
                    break;
                case Event.EVENT_TYPE_TEAM:
                    eventType = "团体";
                    break;
            }

            // 获取性别限制名称（防止空指针）
            String genderLimit = event.getGenderLimitName() != null ?
                    event.getGenderLimitName() : "不限";

            Object[] row = {
                    event.getEventId(),
                    event.getEventName(),
                    eventType,
                    genderLimit,
                    startTime,  // 使用安全处理后的时间字符串
                    event.getLocation(),
                    "报名"
            };
            model.addRow(row);
        }
    }

    private void enrollToSelectedEvent() {
        int selectedRow = eventsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "请选择要报名的活动",
                    "提示",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int eventId = (int) eventsTable.getValueAt(selectedRow, 0);
        if (controller.enrollToEvent(eventId)) {
            JOptionPane.showMessageDialog(this,
                    "报名成功，等待审核",
                    "成功",
                    JOptionPane.INFORMATION_MESSAGE);
            loadEvents(); // 刷新列表
        } else {
            JOptionPane.showMessageDialog(this,
                    "报名失败，请检查性别限制或是否重复报名",
                    "错误",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}