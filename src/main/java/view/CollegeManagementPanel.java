package view;

import controller.AdminController;
import model.User;
import service.CollegeService;
import util.DatabaseUtil;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.List;

public class CollegeManagementPanel extends JPanel {
    private final AdminController adminController;
    private final CollegeService collegeService;
    private DefaultListModel<String> listModel;
    private JLabel countLabel;
    private JList<String> collegeList;

    public CollegeManagementPanel(AdminController adminController) {
        this.adminController = adminController;
        this.collegeService = new CollegeService();

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        initUI();
        loadColleges();
    }


    private void initUI() {
        // 标题面板
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        JLabel titleLabel = new JLabel("院系列表管理");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 18));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("添加、编辑或删除系统中的院系");
        subtitleLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        subtitleLabel.setForeground(Color.GRAY);
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        titlePanel.add(titleLabel);
        titlePanel.add(Box.createVerticalStrut(5));
        titlePanel.add(subtitleLabel);
        add(titlePanel, BorderLayout.NORTH);

        // 中心面板 - 包含院系列表
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        // 院系列表
        JPanel listPanel = new JPanel(new BorderLayout());
        listPanel.setBorder(BorderFactory.createTitledBorder("院系列表"));

        listModel = new DefaultListModel<>();
        collegeList = new JList<>(listModel);
        collegeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        collegeList.setFont(new Font("微软雅黑", Font.PLAIN, 14));

        JScrollPane scrollPane = new JScrollPane(collegeList);
        listPanel.add(scrollPane, BorderLayout.CENTER);
        centerPanel.add(listPanel, BorderLayout.CENTER);

        // 操作按钮面板
        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JButton deleteButton = new JButton("删除院系");
        deleteButton.setBackground(new Color(220, 80, 60));
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setEnabled(false);

        JButton addButton = new JButton("添加院系");
        JButton editButton = new JButton("编辑院系");
        editButton.setEnabled(false);
        JButton refreshButton = new JButton("刷新列表");

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);

        centerPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(centerPanel, BorderLayout.CENTER);

        // 底部信息面板
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        infoPanel.setBorder(BorderFactory.createTitledBorder("统计信息"));

        countLabel = new JLabel("院系总数: 0");
        countLabel.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        infoPanel.add(countLabel);

        add(infoPanel, BorderLayout.SOUTH);

        // 事件监听器
        collegeList.addListSelectionListener(e -> {
            boolean hasSelection = !collegeList.isSelectionEmpty();
            deleteButton.setEnabled(hasSelection);
            editButton.setEnabled(hasSelection);
        });

        addButton.addActionListener(e -> showAddCollegeDialog());

        editButton.addActionListener(e -> editSelectedCollege());

        deleteButton.addActionListener(e -> deleteSelectedCollege());

        refreshButton.addActionListener(e -> loadColleges());
    }

    private void loadColleges() {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            private List<String> colleges;

            @Override
            protected Void doInBackground() {
                colleges = collegeService.getAllColleges();
                return null;
            }

            @Override
            protected void done() {
                listModel.clear();
                for (String college : colleges) {
                    listModel.addElement(college);
                }
                collegeList.clearSelection();
                countLabel.setText("院系总数: " + colleges.size());
            }
        };
        worker.execute();
    }

    private void showAddCollegeDialog() {
        JTextField nameField = new JTextField(20);

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("院系名称:"));
        panel.add(nameField);

        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "添加新院系",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            String collegeName = nameField.getText().trim();
            if (!collegeName.isEmpty()) {
                if (collegeService.addCollege(collegeName)) {
                    listModel.addElement(collegeName);
                    countLabel.setText("院系总数: " + listModel.size());
                    JOptionPane.showMessageDialog(this, "院系添加成功！");
                } else {
                    JOptionPane.showMessageDialog(this,
                            "添加失败: 院系名称已存在", "添加失败", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this,
                        "请输入院系名称", "输入错误", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    private void editSelectedCollege() {
        int selectedIndex = collegeList.getSelectedIndex();
        if (selectedIndex != -1) {
            String originalName = listModel.getElementAt(selectedIndex);

            JTextField nameField = new JTextField(originalName, 20);

            JPanel panel = new JPanel(new GridLayout(0, 1));
            panel.add(new JLabel("院系名称:"));
            panel.add(nameField);

            int result = JOptionPane.showConfirmDialog(
                    this,
                    panel,
                    "编辑院系",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE
            );

            if (result == JOptionPane.OK_OPTION) {
                String newName = nameField.getText().trim();
                if (!newName.isEmpty() && !newName.equals(originalName)) {
                    if (collegeService.updateCollege(originalName, newName)) {
                        listModel.setElementAt(newName, selectedIndex);
                        JOptionPane.showMessageDialog(this, "院系修改成功！");
                    } else {
                        JOptionPane.showMessageDialog(this,
                                "修改失败: 院系名称已存在", "修改失败", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        }
    }

    private void deleteSelectedCollege() {
        int selectedIndex = collegeList.getSelectedIndex();
        if (selectedIndex != -1) {
            String collegeName = listModel.getElementAt(selectedIndex);

            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "确定要删除 '" + collegeName + "' 吗？",
                    "确认删除",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm == JOptionPane.YES_OPTION) {
                if (collegeService.deleteCollege(collegeName)) {
                    listModel.remove(selectedIndex);
                    countLabel.setText("院系总数: " + listModel.size());
                    JOptionPane.showMessageDialog(this, "院系删除成功！");
                } else {
                    JOptionPane.showMessageDialog(
                            this,
                            "删除 '" + collegeName + "' 失败！该院系可能有关联数据",
                            "删除失败",
                            JOptionPane.WARNING_MESSAGE
                    );
                }
            }
        }
    }
}