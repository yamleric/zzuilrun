package view;

import model.Event;
import service.EventService;

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
    private final EventService eventService;
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

    // 修改构造函数，接收EventService
    public EventDetailDialog(Window owner, Event event, EventService eventService) {
        super(owner, event == null ? "添加比赛项目" : "编辑比赛项目", ModalityType.APPLICATION_MODAL);
        this.event = event == null ? new Event() : event;
        this.eventService = eventService;

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
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 项目名称
        JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        namePanel.add(new JLabel("项目名称:"));
        nameField = new JTextField(event.getEventName(), 20);
        namePanel.add(nameField);
        formPanel.add(namePanel);

        // 项目类型
        JPanel typePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        typePanel.add(new JLabel("项目类型:"));
        typeComboBox = new JComboBox<>(new String[]{"田径", "游泳", "球类", "体操", "其他"});
        if (event.getEventType() >= 0 && event.getEventType() <= 4) {
            typeComboBox.setSelectedIndex(event.getEventType());
        }
        typePanel.add(typeComboBox);
        formPanel.add(typePanel);

        // 性别限制
        JPanel genderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        genderPanel.add(new JLabel("性别限制:"));
        genderLimitComboBox = new JComboBox<>(new String[]{"男", "女", "不限"});
        if (event.getGenderLimit() != null) {
            switch (event.getGenderLimit()) {
                case "M": genderLimitComboBox.setSelectedItem("男"); break;
                case "F": genderLimitComboBox.setSelectedItem("女"); break;
                default: genderLimitComboBox.setSelectedItem("不限");
            }
        }
        genderPanel.add(genderLimitComboBox);
        formPanel.add(genderPanel);

        // 人数设置面板
        JPanel participantsPanel = new JPanel();
        participantsPanel.setLayout(new GridLayout(2, 2, 5, 5));

        // 最小人数
        participantsPanel.add(new JLabel("最小人数:"));
        int minPartValue = event.getMinParticipants() > 0 ? event.getMinParticipants() : 1;
        minParticipantsSpinner = new JSpinner(new SpinnerNumberModel(
                minPartValue,
                1,
                100,
                1
        ));
        participantsPanel.add(minParticipantsSpinner);

        // 最大人数
        participantsPanel.add(new JLabel("最大人数:"));
        int maxPartValue = Math.max(event.getMaxParticipants(), minPartValue);
        maxParticipantsSpinner = new JSpinner(new SpinnerNumberModel(
                maxPartValue,
                minPartValue,
                100,
                1
        ));
        participantsPanel.add(maxParticipantsSpinner);

        // 人数关联逻辑
        minParticipantsSpinner.addChangeListener(e -> {
            int min = (Integer) minParticipantsSpinner.getValue();
            SpinnerNumberModel maxModel = (SpinnerNumberModel) maxParticipantsSpinner.getModel();
            maxModel.setMinimum(min);
            if ((Integer) maxModel.getValue() < min) {
                maxModel.setValue(min);
            }
        });

        formPanel.add(participantsPanel);

        // 开始时间面板
        JPanel startTimePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        startTimePanel.add(new JLabel("开始时间:"));
        startDateField = new JTextField(10);
        startDateField.setToolTipText("格式: yyyy-MM-dd");
        if (event.getStartTime() != null) {
            startDateField.setText(event.getStartTime().format(DATE_FORMATTER));
        }
        startTimePanel.add(startDateField);

        startTimePanel.add(new JLabel("时间:"));
        startTimeField = new JTextField(5);
        startTimeField.setToolTipText("格式: HH:mm");
        if (event.getStartTime() != null) {
            startTimeField.setText(String.format("%02d:%02d",
                    event.getStartTime().getHour(), event.getStartTime().getMinute()));
        }
        startTimePanel.add(startTimeField);
        formPanel.add(startTimePanel);

        // 结束时间面板
        JPanel endTimePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        endTimePanel.add(new JLabel("结束时间:"));
        endDateField = new JTextField(10);
        endDateField.setToolTipText("格式: yyyy-MM-dd");
        if (event.getEndTime() != null) {
            endDateField.setText(event.getEndTime().format(DATE_FORMATTER));
        }
        endTimePanel.add(endDateField);

        endTimePanel.add(new JLabel("时间:"));
        endTimeField = new JTextField(5);
        endTimeField.setToolTipText("格式: HH:mm");
        if (event.getEndTime() != null) {
            endTimeField.setText(String.format("%02d:%02d",
                    event.getEndTime().getHour(), event.getEndTime().getMinute()));
        }
        endTimePanel.add(endTimeField);
        formPanel.add(endTimePanel);

        // 添加时间输入监听器
        addDateTimeListeners();

        // 地点
        JPanel locationPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        locationPanel.add(new JLabel("地点:"));
        locationField = new JTextField(event.getLocation(), 20);
        locationPanel.add(locationField);
        formPanel.add(locationPanel);

        // 描述
        JPanel descPanel = new JPanel(new BorderLayout());
        descPanel.add(new JLabel("描述:"), BorderLayout.NORTH);
        descriptionArea = new JTextArea(event.getDescription(), 5, 30);
        descPanel.add(new JScrollPane(descriptionArea), BorderLayout.CENTER);
        formPanel.add(descPanel);

        // 状态
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.add(new JLabel("状态:"));
        statusComboBox = new JComboBox<>(new String[]{"禁用", "启用"});
        statusComboBox.setSelectedIndex(event.getStatus());
        statusPanel.add(statusComboBox);
        formPanel.add(statusPanel);

        return formPanel;
    }

    private void addDateTimeListeners() {
        DocumentListener timeListener = new DocumentListener() {
            @Override public void changedUpdate(DocumentEvent e) { validateDateTime(); }
            @Override public void insertUpdate(DocumentEvent e) { validateDateTime(); }
            @Override public void removeUpdate(DocumentEvent e) { validateDateTime(); }
        };

        startDateField.getDocument().addDocumentListener(timeListener);
        startTimeField.getDocument().addDocumentListener(timeListener);
        endDateField.getDocument().addDocumentListener(timeListener);
        endTimeField.getDocument().addDocumentListener(timeListener);
    }

    private void validateDateTime() {
        try {
            LocalDateTime startTime = parseDateTime(startDateField.getText(), startTimeField.getText());
            LocalDateTime endTime = parseDateTime(endDateField.getText(), endTimeField.getText());

            if (startTime != null && endTime != null) {
                if (endTime.isBefore(startTime)) {
                    timeErrorLabel.setText("错误: 结束时间不能早于开始时间");
                } else {
                    timeErrorLabel.setText("");
                }
            } else {
                timeErrorLabel.setText("");
            }
        } catch (DateTimeParseException e) {
            timeErrorLabel.setText("错误: 时间格式无效 - 使用 yyyy-MM-dd HH:mm");
        }
    }

    private LocalDateTime parseDateTime(String datePart, String timePart) throws DateTimeParseException {
        if ((datePart == null || datePart.isEmpty()) || (timePart == null || timePart.isEmpty())) {
            return null;
        }

        String dateTime = datePart + " " + timePart;
        return LocalDateTime.parse(dateTime, DATE_TIME_FORMATTER);
    }

    private void saveEvent(ActionEvent e) {
        try {
            // 设置基本字段
            event.setEventName(nameField.getText().trim());
            event.setEventType(typeComboBox.getSelectedIndex());

            String genderOption = (String) genderLimitComboBox.getSelectedItem();
            if ("男".equals(genderOption)) event.setGenderLimit("M");
            else if ("女".equals(genderOption)) event.setGenderLimit("F");
            else event.setGenderLimit("A");

            event.setMinParticipants((Integer) minParticipantsSpinner.getValue());
            event.setMaxParticipants((Integer) maxParticipantsSpinner.getValue());
            event.setLocation(locationField.getText().trim());
            event.setDescription(descriptionArea.getText().trim());
            event.setStatus(statusComboBox.getSelectedIndex());

            // 解析并验证时间
            LocalDateTime startTime = parseDateTime(startDateField.getText(), startTimeField.getText());
            LocalDateTime endTime = parseDateTime(endDateField.getText(), endTimeField.getText());

            if (startTime != null && endTime != null) {
                if (endTime.isBefore(startTime)) {
                    throw new DateTimeParseException("结束时间早于开始时间", "", 0);
                }
                event.setStartTime(startTime);
                event.setEndTime(endTime);
            }

            // 调用服务保存事件
            if (event.getEventId() > 0) {
                // 更新现有事件
                saved = eventService.updateEvent(event);
            } else {
                // 插入新事件
                int newId = eventService.insertEvent(event);
                saved = newId > 0;
            }

            if (saved) {
                dispose(); // 保存成功后关闭对话框
            } else {
                JOptionPane.showMessageDialog(this,
                        "保存失败，请检查系统状态",
                        "保存错误",
                        JOptionPane.ERROR_MESSAGE);
            }

        } catch (DateTimeParseException ex) {
            timeErrorLabel.setText("错误: 时间格式无效 - " + ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    "请正确填写日期时间格式 (yyyy-MM-dd HH:mm)\n例如: 2023-10-15 14:30",
                    "时间格式错误",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "保存时发生错误: " + ex.getMessage(),
                    "系统错误",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    public boolean isSaved() {
        return saved;
    }
}