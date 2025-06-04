package view;

import controller.AdminController;
import model.Event;
import service.EventService;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.List;

public class EventManagementPanel extends JPanel {
    private final AdminController adminController;
    private final EventService eventService;
    private EventTableModel tableModel;

    public EventManagementPanel(AdminController adminController) {
        this.adminController = adminController;
        this.eventService = new EventService();

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        initUI();
        loadEvents();
    }

    private void initUI() {
        // 标题
        JLabel titleLabel = new JLabel("比赛项目管理");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 20));
        add(titleLabel, BorderLayout.NORTH);

        // 表格和滚动面板
        tableModel = new EventTableModel();
        JTable eventTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(eventTable);
        add(scrollPane, BorderLayout.CENTER);

        // 工具栏
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setLayout(new FlowLayout(FlowLayout.RIGHT));

        JButton addButton = new JButton("添加项目");
        JButton editButton = new JButton("编辑项目");
        JButton deleteButton = new JButton("删除项目");
        JButton importButton = new JButton("导入项目");
        JButton exportButton = new JButton("导出项目");

        toolBar.add(addButton);
        toolBar.add(editButton);
        toolBar.add(deleteButton);
        toolBar.add(importButton);
        toolBar.add(exportButton);

        // 按钮事件
        addButton.addActionListener(e -> showAddEventDialog());
        editButton.addActionListener(e -> editSelectedEvent(eventTable));
        deleteButton.addActionListener(e -> deleteSelectedEvents(eventTable));
        importButton.addActionListener(e -> importEvents());
        exportButton.addActionListener(e -> exportEvents());

        add(toolBar, BorderLayout.SOUTH);
    }

    private void loadEvents() {
        List<Event> events = eventService.getAllEvents();
        tableModel.setEvents(events);
    }

    private void showAddEventDialog() {
        // 实现添加对话框
        EventDetailDialog dialog = new EventDetailDialog(SwingUtilities.getWindowAncestor(this), null);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            loadEvents();
        }
    }

    private void editSelectedEvent(JTable table) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            Event event = tableModel.getEventAt(table.convertRowIndexToModel(selectedRow));
            EventDetailDialog dialog = new EventDetailDialog(SwingUtilities.getWindowAncestor(this), event);
            dialog.setVisible(true);
            if (dialog.isSaved()) {
                loadEvents();
            }
        } else {
            JOptionPane.showMessageDialog(this, "请先选择一个项目", "提示", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void deleteSelectedEvents(JTable table) {
        int[] selectedRows = table.getSelectedRows();
        if (selectedRows.length == 0) {
            JOptionPane.showMessageDialog(this, "请先选择要删除的项目", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "确定要删除选中的 " + selectedRows.length + " 个项目吗？",
                "确认删除",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            for (int i = selectedRows.length - 1; i >= 0; i--) {
                int modelRow = table.convertRowIndexToModel(selectedRows[i]);
                Event event = tableModel.getEventAt(modelRow);
                eventService.deleteEvent(event.getEventId());
            }
            loadEvents();
        }
    }

    private void importEvents() {
        JOptionPane.showMessageDialog(this, "导入功能待实现", "提示", JOptionPane.INFORMATION_MESSAGE);
    }

    private void exportEvents() {
        JOptionPane.showMessageDialog(this, "导出功能待实现", "提示", JOptionPane.INFORMATION_MESSAGE);
    }

    // 项目表格模型
    class EventTableModel extends AbstractTableModel {
        private final String[] COLUMN_NAMES = {
                "项目ID", "项目名称", "类型", "性别限制",
                "最小人数", "最大人数", "开始时间", "结束时间",
                "地点", "状态"
        };

        private List<Event> events;

        public void setEvents(List<Event> events) {
            this.events = events;
            fireTableDataChanged();
        }

        public Event getEventAt(int row) {
            return events.get(row);
        }

        @Override
        public int getRowCount() {
            return events == null ? 0 : events.size();
        }

        @Override
        public int getColumnCount() {
            return COLUMN_NAMES.length;
        }

        @Override
        public String getColumnName(int column) {
            return COLUMN_NAMES[column];
        }

        // 在 EventTableModel 中
        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Event event = events.get(rowIndex);
            switch (columnIndex) {
                case 0: return event.getEventId();
                case 1: return event.getEventName();
                case 2: return event.getEventTypeName(); // 使用辅助方法
                case 3: return event.getGenderLimitName(); // 使用辅助方法
                case 4: return event.getMinParticipants();
                case 5: return event.getMaxParticipants();
                case 6: return event.getStartTime();
                case 7: return event.getEndTime();
                case 8: return event.getLocation();
                case 9: return event.getStatusName(); // 使用辅助方法
                default: return null;
            }
        }
        private String getEventTypeName(int type) {
            switch (type) {
                case 1: return "田径";
                case 2: return "游泳";
                case 3: return "球类";
                case 4: return "体操";
                default: return "其他";
            }
        }
    }
}