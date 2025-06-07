package view;

import controller.AdminController;
import model.ArrangementGroup;
import model.Event;
import model.TrackAssignment;
import service.ArrangementService;
import util.DatabaseUtil;
import util.DateUtil;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public class ArrangementPanel extends JPanel {
    private final AdminController controller;
    private final ArrangementService arrangementService;
    private JComboBox<Event> eventComboBox;
    private JButton arrangeBtn;
    private JButton saveBtn;
    private JButton generateListBtn;
    private JTable groupsTable;
    private JTable assignmentsTable;
    private JButton timeSetBtn;
    private JButton exportListBtn; // 导出按钮声明

    private List<ArrangementGroup> currentGroups;
    private Event selectedEvent; // 添加selectedEvent变量声明

    public ArrangementPanel(AdminController controller) {
        this.controller = controller;
        this.arrangementService = new ArrangementService(); // 初始化编排服务
        initUI();
        loadEvents();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // 顶部面板 - 项目选择和操作按钮
        JPanel topPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        topPanel.add(new JLabel("选择项目:"));
        eventComboBox = new JComboBox<>();
        eventComboBox.addActionListener(e -> {
            selectedEvent = (Event) eventComboBox.getSelectedItem(); // 设置选中的项目
            clearTables();
        });
        topPanel.add(eventComboBox);

        arrangeBtn = new JButton("自动分组");
        arrangeBtn.addActionListener(e -> arrangeGroups());
        topPanel.add(arrangeBtn);

        saveBtn = new JButton("保存编排");
        saveBtn.setEnabled(false);
        saveBtn.addActionListener(e -> saveArrangement());
        topPanel.add(saveBtn);

        generateListBtn = new JButton("生成比赛名单");
        generateListBtn.setEnabled(false);
        generateListBtn.addActionListener(e -> generateStartList());
        topPanel.add(generateListBtn);

        // 添加导出按钮
        exportListBtn = new JButton("导出比赛名单");
        exportListBtn.addActionListener(e -> exportStartList());
        topPanel.add(exportListBtn);

        add(topPanel, BorderLayout.NORTH);

        // 中间面板 - 分组和道次分配
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.5);

        // 分组面板
        JPanel groupsPanel = new JPanel(new BorderLayout());
        groupsPanel.setBorder(BorderFactory.createTitledBorder("分组信息"));
        groupsPanel.add(new JLabel("比赛分组:"), BorderLayout.NORTH);

        // 分组表格
        String[] groupColumns = {"组名", "类型", "比赛时间", "人数", "操作"};
        DefaultTableModel groupModel = new DefaultTableModel(groupColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4; // 只有操作列可编辑
            }
        };

        groupsTable = new JTable(groupModel);
        groupsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                showAssignments();
            }
        });

        timeSetBtn = new JButton("设置时间");
        timeSetBtn.setEnabled(false);
        timeSetBtn.addActionListener(e -> setGroupTime());

        JPanel groupBtnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        groupBtnPanel.add(timeSetBtn);

        groupsPanel.add(new JScrollPane(groupsTable), BorderLayout.CENTER);
        groupsPanel.add(groupBtnPanel, BorderLayout.SOUTH);

        // 道次分配面板
        JPanel assignmentsPanel = new JPanel(new BorderLayout());
        assignmentsPanel.setBorder(BorderFactory.createTitledBorder("道次分配"));
        assignmentsPanel.add(new JLabel("道次分配:"), BorderLayout.NORTH);

        // 道次分配表格
        String[] assignmentColumns = {"姓名", "学院", "赛道", "道次"};
        DefaultTableModel assignmentModel = new DefaultTableModel(assignmentColumns, 0);
        assignmentsTable = new JTable(assignmentModel);

        assignmentsPanel.add(new JScrollPane(assignmentsTable), BorderLayout.CENTER);

        splitPane.setTopComponent(groupsPanel);
        splitPane.setBottomComponent(assignmentsPanel);

        add(splitPane, BorderLayout.CENTER);
    }

    private void loadEvents() {
        eventComboBox.removeAllItems();
        List<Event> events = controller.getEventsForArrangement();
        events.forEach(eventComboBox::addItem);

        // 初始化选中的项目
        if (eventComboBox.getItemCount() > 0) {
            eventComboBox.setSelectedIndex(0);
            selectedEvent = (Event) eventComboBox.getSelectedItem();
        }
    }

    private void arrangeGroups() {
        selectedEvent = (Event) eventComboBox.getSelectedItem(); // 确保设置选中的项目
        if (selectedEvent != null) {
            currentGroups = controller.autoGroup(selectedEvent);
            populateGroupsTable();
            saveBtn.setEnabled(true);
            timeSetBtn.setEnabled(true);
        }
    }

    private void populateGroupsTable() {
        DefaultTableModel model = (DefaultTableModel) groupsTable.getModel();
        model.setRowCount(0);

        for (ArrangementGroup group : currentGroups) {
            model.addRow(new Object[]{
                    group.getGroupName(),
                    group.getGroupType(),
                    group.getStartTime() != null ?
                            DateUtil.formatDateTime(group.getStartTime()) : "未设置",
                    group.getParticipants().size(),
                    "设置时间"
            });
        }
    }

    private void showAssignments() {
        int selectedRow = groupsTable.getSelectedRow();
        if (selectedRow >= 0 && selectedRow < currentGroups.size()) {
            ArrangementGroup group = currentGroups.get(selectedRow);
            DefaultTableModel model = (DefaultTableModel) assignmentsTable.getModel();
            model.setRowCount(0);

            for (TrackAssignment assignment : group.getAssignments()) {
                model.addRow(new Object[]{
                        assignment.getEnrollment().getUser().getRealName(),
                        DatabaseUtil.fetchCollegeName(assignment.getEnrollment().getUser().getCollegeId()),
                        assignment.getTrackNumber(),
                        assignment.getLaneNumber()
                });
            }
        }
    }

    private void setGroupTime() {
        int selectedRow = groupsTable.getSelectedRow();
        if (selectedRow >= 0 && selectedRow < currentGroups.size()) {
            ArrangementGroup group = currentGroups.get(selectedRow);

            // 使用JCalendar或自定义时间选择器
            LocalDateTime currentTime = group.getStartTime() != null ?
                    group.getStartTime() : LocalDateTime.now();

            // 简单实现 - 实际应用中应使用更好的时间选择器
            String input = JOptionPane.showInputDialog(
                    this,
                    "设置比赛时间 (YYYY-MM-DD HH:mm):",
                    DateUtil.formatDateTime(currentTime)
            );

            if (input != null && !input.isEmpty()) {
                try {
                    LocalDateTime newTime = DateUtil.parseDateTime(input);
                    group.setStartTime(newTime);
                    controller.setGroupTime(group, newTime);
                    populateGroupsTable(); // 刷新表格
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "时间格式错误！", "错误", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void saveArrangement() {
        if (currentGroups != null && !currentGroups.isEmpty()) {
            // 检查所有分组是否设置了时间
            for (ArrangementGroup group : currentGroups) {
                if (group.getStartTime() == null) {
                    JOptionPane.showMessageDialog(this,
                            "请为所有分组设置比赛时间！",
                            "未设置时间",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }

            controller.saveArrangement(currentGroups);
            JOptionPane.showMessageDialog(this, "编排结果保存成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
            generateListBtn.setEnabled(true);
            saveBtn.setEnabled(false);
        }
    }

    private void generateStartList() {
        selectedEvent = (Event) eventComboBox.getSelectedItem(); // 确保当前选中的项目
        if (selectedEvent != null) {
            controller.generateStartList(selectedEvent);
            JOptionPane.showMessageDialog(this, "比赛名单生成成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void clearTables() {
        ((DefaultTableModel) groupsTable.getModel()).setRowCount(0);
        ((DefaultTableModel) assignmentsTable.getModel()).setRowCount(0);
        saveBtn.setEnabled(false);
        generateListBtn.setEnabled(false);
        timeSetBtn.setEnabled(false);
        currentGroups = null;
    }

    private void exportStartList() {
        selectedEvent = (Event) eventComboBox.getSelectedItem(); // 确保当前选中的项目

        if (selectedEvent == null) {
            JOptionPane.showMessageDialog(this, "请先选择一个项目", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("导出比赛名单");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Excel文件 (*.xlsx)", "xlsx"));

        int returnValue = fileChooser.showSaveDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String filePath = selectedFile.getAbsolutePath();

            if (!filePath.endsWith(".xlsx")) {
                filePath += ".xlsx";
            }

            try {
                arrangementService.exportStartListToExcel(selectedEvent, filePath);
                JOptionPane.showMessageDialog(this, "比赛名单导出成功", "成功", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "导出失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }
}