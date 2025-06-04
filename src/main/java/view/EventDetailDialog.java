package view;

import model.Event;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class EventDetailDialog extends JDialog {
    private Event event;
    private boolean saved = false;

    // 日期时间格式和解析器
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // 表单组件
    private JTextField nameField;
    private JComboBox<String> typeComboBox;
    private JComboBox<String> genderLimitComboBox;
    private JSpinner minParticipantsSpinner;
    private JSpinner maxParticipantsSpinner;
    private JTextField startDateField;
    private JTextField startTimeField;
    private JTextField endDateField;
    private JTextField endTimeField;
    private JTextField locationField;
    private JTextArea descriptionArea;
    private JComboBox<String> statusComboBox;
    private JLabel timeErrorLabel;

    public EventDetailDialog(Window owner, Event event) {
        super(owner, event == null ? "添加比赛项目" : "编辑比赛项目", ModalityType.APPLICATION_MODAL);
        this.event = event == null ? new Event() : event;

        setSize(600, 600);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        initUI();
    }

    private void initUI() {
        // 主面板
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // 滚动面板用于容纳表单内容
        JScrollPane scrollPane = new JScrollPane(createFormPanel());
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // 添加时间错误提示
        timeErrorLabel = new JLabel(" ");
        timeErrorLabel.setForeground(Color.RED);
        mainPanel.add(timeErrorLabel, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);

        // 按钮面板
        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("保存");
        saveButton.addActionListener(this::saveEvent);
        JButton cancelButton = new JButton("取消");
        cancelButton.addActionListener(e -> dispose());
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));

        // 项目名称
        formPanel.add(new JLabel("项目名称:"));
        nameField = new JTextField(event.getEventName());
        formPanel.add(nameField);

        // 项目类型
        formPanel.add(new JLabel("项目类型:"));
        typeComboBox = new JComboBox<>(new String[]{"田径", "游泳", "球类", "体操", "其他"});
        typeComboBox.setSelectedIndex(event.getEventType());
        formPanel.add(typeComboBox);

        // 性别限制
        formPanel.add(new JLabel("性别限制:"));
        genderLimitComboBox = new JComboBox<>(new String[]{"男", "女", "不限"});
        if (event.getGenderLimit() != null) {
            switch (event.getGenderLimit()) {
                case "M": genderLimitComboBox.setSelectedItem("男"); break;
                case "F": genderLimitComboBox.setSelectedItem("女"); break;
                default: genderLimitComboBox.setSelectedItem("不限");
            }
        }
        formPanel.add(genderLimitComboBox);

// 最小人数
        formPanel.add(new JLabel("最小人数:"));
// 如果event中的最小人数有效，使用该值；否则使用1
        int minPartValue = event.getMinParticipants() > 0 ? event.getMinParticipants() : 1;
        minParticipantsSpinner = new JSpinner(new SpinnerNumberModel(
                minPartValue,
                1,
                100,
                1
        ));
        formPanel.add(minParticipantsSpinner);

// 最大人数
        formPanel.add(new JLabel("最大人数:"));
// 确保初始最大值不小于最小值
        int maxPartValue = Math.max(event.getMaxParticipants(), minPartValue);
        maxParticipantsSpinner = new JSpinner(new SpinnerNumberModel(
                maxPartValue,
                minPartValue, // 最小值设置为当前的最小人数值
                100,
                1
        ));
        formPanel.add(maxParticipantsSpinner);

// 确保最小人数 <= 最大人数
        minParticipantsSpinner.addChangeListener(e -> {
            int min = (Integer) minParticipantsSpinner.getValue();
            int currentMax = (Integer) maxParticipantsSpinner.getValue();

            // 如果当前最大值小于新的最小值，则更新为新的最小值
            if (currentMax < min) {
                maxParticipantsSpinner.setValue(min);
            }

            // 更新最大值微调器的最小值限制
            SpinnerNumberModel maxModel = (SpinnerNumberModel) maxParticipantsSpinner.getModel();
            maxModel.setMinimum(min);
        });
        formPanel.add(maxParticipantsSpinner);

        // 开始日期
        formPanel.add(new JLabel("开始日期 (yyyy-MM-dd):"));
        JPanel startDatePanel = new JPanel(new GridLayout(1, 2));
        startDateField = new JTextField();
        if (event.getStartTime() != null) {
            startDateField.setText(event.getStartTime().format(DATE_FORMATTER));
        }
        startDatePanel.add(startDateField);
        startDatePanel.add(new JLabel("时间 (HH:mm):"));
        startTimeField = new JTextField(event.getStartTime() != null ?
                event.getStartTime().toLocalTime().toString().substring(0, 5) : "");
        startDatePanel.add(startTimeField);
        formPanel.add(startDatePanel);

        // 结束日期
        formPanel.add(new JLabel("结束日期 (yyyy-MM-dd):"));
        JPanel endDatePanel = new JPanel(new GridLayout(1, 2));
        endDateField = new JTextField();
        if (event.getEndTime() != null) {
            endDateField.setText(event.getEndTime().format(DATE_FORMATTER));
        }
        endDatePanel.add(endDateField);
        endDatePanel.add(new JLabel("时间 (HH:mm):"));
        endTimeField = new JTextField(event.getEndTime() != null ?
                event.getEndTime().toLocalTime().toString().substring(0, 5) : "");
        endDatePanel.add(endTimeField);
        formPanel.add(endDatePanel);

        // 添加时间输入监听器
        addDateTimeListeners();

        // 地点
        formPanel.add(new JLabel("地点:"));
        locationField = new JTextField(event.getLocation());
        formPanel.add(locationField);

        // 描述
        formPanel.add(new JLabel("描述:"));
        descriptionArea = new JTextArea(event.getDescription());
        descriptionArea.setRows(3);
        formPanel.add(new JScrollPane(descriptionArea));

        // 状态
        formPanel.add(new JLabel("状态:"));
        statusComboBox = new JComboBox<>(new String[]{"关闭", "开放报名", "已结束"});
        statusComboBox.setSelectedIndex(event.getStatus());
        formPanel.add(statusComboBox);

        return formPanel;
    }

    private void addDateTimeListeners() {
        // 用于共享的文档监听器
        DocumentListener timeListener = new DocumentListener() {
            @Override public void changedUpdate(DocumentEvent e) { validateDateTime(); }
            @Override public void insertUpdate(DocumentEvent e) { validateDateTime(); }
            @Override public void removeUpdate(DocumentEvent e) { validateDateTime(); }
        };

        // 添加监听器到所有日期时间字段
        startDateField.getDocument().addDocumentListener(timeListener);
        startTimeField.getDocument().addDocumentListener(timeListener);
        endDateField.getDocument().addDocumentListener(timeListener);
        endTimeField.getDocument().addDocumentListener(timeListener);
    }

    private void validateDateTime() {
        try {
            // 解析开始时间
            LocalDateTime startTime = parseDateTime(startDateField.getText(), startTimeField.getText());

            // 解析结束时间
            LocalDateTime endTime = parseDateTime(endDateField.getText(), endTimeField.getText());

            // 检查时间关系
            if (startTime != null && endTime != null && endTime.isBefore(startTime)) {
                timeErrorLabel.setText("结束时间不能早于开始时间");
            } else {
                timeErrorLabel.setText(" ");
            }
        } catch (DateTimeParseException e) {
            timeErrorLabel.setText("时间格式错误: " + e.getMessage());
        }
    }

    private LocalDateTime parseDateTime(String datePart, String timePart) throws DateTimeParseException {
        if (datePart == null || datePart.isEmpty() || timePart == null || timePart.isEmpty()) {
            return null;
        }

        String fullDateTime = datePart + " " + timePart;
        return LocalDateTime.parse(fullDateTime, DATE_TIME_FORMATTER);
    }

    private void saveEvent(ActionEvent e) {
        try {
            // 验证并设置时间
            validateAndSetTimes();

            // 更新事件对象
            event.setEventName(nameField.getText().trim());
            event.setEventType(typeComboBox.getSelectedIndex());

            // 设置性别限制
            String genderOption = (String) genderLimitComboBox.getSelectedItem();
            if ("男".equals(genderOption)) event.setGenderLimit("M");
            else if ("女".equals(genderOption)) event.setGenderLimit("F");
            else event.setGenderLimit("A");

            event.setMinParticipants((Integer) minParticipantsSpinner.getValue());
            event.setMaxParticipants((Integer) maxParticipantsSpinner.getValue());
            event.setLocation(locationField.getText().trim());
            event.setDescription(descriptionArea.getText().trim());
            event.setStatus(statusComboBox.getSelectedIndex());

            // 调用服务保存
            saved = true;
            dispose();

        } catch (DateTimeParseException ex) {
            timeErrorLabel.setText("时间格式错误: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, "请正确填写日期时间格式 (yyyy-MM-dd HH:mm)",
                    "输入错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void validateAndSetTimes() throws DateTimeParseException {
        // 设置开始时间
        if (!startDateField.getText().isEmpty() && !startTimeField.getText().isEmpty()) {
            String fullStart = startDateField.getText() + " " + startTimeField.getText();
            event.setStartTime(LocalDateTime.parse(fullStart, DATE_TIME_FORMATTER));
        }

        // 设置结束时间
        if (!endDateField.getText().isEmpty() && !endTimeField.getText().isEmpty()) {
            String fullEnd = endDateField.getText() + " " + endTimeField.getText();
            event.setEndTime(LocalDateTime.parse(fullEnd, DATE_TIME_FORMATTER));
        }

        // 验证时间顺序
        if (event.getStartTime() != null && event.getEndTime() != null) {
            if (event.getEndTime().isBefore(event.getStartTime())) {
                throw new DateTimeParseException("结束时间早于开始时间", "", 0);
            }
        }
    }

    public boolean isSaved() {
        return saved;
    }
}