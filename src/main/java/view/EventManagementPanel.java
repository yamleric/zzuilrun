package view;

import controller.AdminController;
import model.Event;
import service.EventService;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
        EventDetailDialog dialog = new EventDetailDialog(
                SwingUtilities.getWindowAncestor(this),
                null,
                eventService
        );
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            loadEvents();
        }
    }

    private void editSelectedEvent(JTable table) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            // 修复：使用 tableModel.getEventAt() 方法
            Event event = tableModel.getEventAt(table.convertRowIndexToModel(selectedRow));

            EventDetailDialog dialog = new EventDetailDialog(
                    SwingUtilities.getWindowAncestor(this),
                    event,
                    eventService
            );
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
                // 修复：使用 tableModel.getEventAt() 方法
                Event event = tableModel.getEventAt(modelRow);
                eventService.deleteEvent(event.getEventId());
            }
            loadEvents();
        }
    }

//    private void importEvents() {
//        JOptionPane.showMessageDialog(this, "导入功能待实现", "提示", JOptionPane.INFORMATION_MESSAGE);
//    }
//
//    private void exportEvents() {
//        JOptionPane.showMessageDialog(this, "导出功能待实现", "提示", JOptionPane.INFORMATION_MESSAGE);
//    }

    // 事件表格模型（已修复所有问题）
    class EventTableModel extends AbstractTableModel {
        private final String[] COLUMN_NAMES = {
                "项目ID", "项目名称", "类型", "性别限制",
                "最小人数", "最大人数", "开始时间", "结束时间",
                "地点", "状态"
        };

        private List<Event> events = new ArrayList<>();

        public void setEvents(List<Event> events) {
            this.events = events != null ? events : new ArrayList<>();
            fireTableDataChanged();
        }

        // 添加缺失的 getEventAt() 方法
        public Event getEventAt(int rowIndex) {
            return events.get(rowIndex);
        }

        // 实现 getRowCount() 方法
        @Override
        public int getRowCount() {
            return events.size();
        }

        // 实现 getColumnCount() 方法
        @Override
        public int getColumnCount() {
            return COLUMN_NAMES.length;
        }

        // 实现 getColumnName() 方法
        @Override
        public String getColumnName(int column) {
            return COLUMN_NAMES[column];
        }

        // 实现 getValueAt() 方法
        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            if (rowIndex >= events.size() || rowIndex < 0) {
                return null;
            }

            Event event = events.get(rowIndex);
            switch (columnIndex) {
                case 0: return event.getEventId();
                case 1: return event.getEventName();
                case 2: return getEventTypeName(event.getEventType());
                case 3: return getGenderLimitName(event.getGenderLimit());
                case 4: return event.getMinParticipants();
                case 5: return event.getMaxParticipants();
                case 6: return formatDateTime(event.getStartTime());
                case 7: return formatDateTime(event.getEndTime());
                case 8: return event.getLocation();
                case 9: return getStatusName(event.getStatus());
                default: return null;
            }
        }

        // 事件类型转换
        private String getEventTypeName(int type) {
            switch (type) {
                case 1: return "田径";
                case 2: return "游泳";
                case 3: return "球类";
                case 4: return "体操";
                default: return "其他";
            }
        }

        // 性别限制转换
        private String getGenderLimitName(String limit) {
            if ("M".equals(limit)) return "男";
            if ("F".equals(limit)) return "女";
            return "无限制";
        }

        // 状态转换
        private String getStatusName(int status) {
            return status == 1 ? "启用" : "禁用";
        }

        // 格式化日期时间
        private String formatDateTime(LocalDateTime dateTime) {
            if (dateTime == null) return "未设置";
            return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        }
    }
    // view/EventManagementPanel.java
    private void importEvents() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("选择导入文件");
        fileChooser.setFileFilter(new FileNameExtensionFilter("CSV or Excel Files", "csv", "xlsx"));

        int returnValue = fileChooser.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String filePath = selectedFile.getAbsolutePath();

            try {
                if (filePath.endsWith(".csv")) {
                    eventService.importEventsFromCsv(filePath);
                } else if (filePath.endsWith(".xlsx")) {
                    eventService.importEventsFromExcel(filePath);
                } else {
                    JOptionPane.showMessageDialog(this, "不支持的文件格式", "错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                loadEvents(); // 刷新表格
                JOptionPane.showMessageDialog(this, "导入成功", "成功", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "导入失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    private void exportEvents() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("选择导出格式");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        // 创建文件过滤器
        FileNameExtensionFilter csvFilter = new FileNameExtensionFilter("CSV文件 (*.csv)", "csv");
        FileNameExtensionFilter excelFilter = new FileNameExtensionFilter("Excel文件 (*.xlsx)", "xlsx");

        fileChooser.addChoosableFileFilter(csvFilter);
        fileChooser.addChoosableFileFilter(excelFilter);
        fileChooser.setAcceptAllFileFilterUsed(false);

        int returnValue = fileChooser.showSaveDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String filePath = selectedFile.getAbsolutePath();

            // 根据选择的文件过滤器添加文件扩展名
            FileFilter fileFilter = fileChooser.getFileFilter();
            if (fileFilter == csvFilter && !filePath.endsWith(".csv")) {
                filePath += ".csv";
            } else if (fileFilter == excelFilter && !filePath.endsWith(".xlsx")) {
                filePath += ".xlsx";
            }

            try {
                if (filePath.endsWith(".csv")) {
                    eventService.exportEventsToCsv(filePath);
                } else if (filePath.endsWith(".xlsx")) {
                    eventService.exportEventsToExcel(filePath);
                }
                JOptionPane.showMessageDialog(this, "导出成功", "成功", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "导出失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }
}