package controller;

import service.EnrollmentService;
//import view.EnrollmentView;
import view.UserProfileFrame;

import javax.swing.*;

public class StudentController {
    private final int studentId;
    private final EnrollmentService enrollmentService;

    public StudentController(int studentId) {
        this.studentId = studentId;
        this.enrollmentService = new EnrollmentService();
    }

    public void showEnrollmentView() {
        // 实际实现会创建EnrollmentView实例
        JOptionPane.showMessageDialog(null, "显示报名界面 - 学生ID: " + studentId);
    }

    public void showMyEnrollments() {
        // 显示已报名的活动
        int count = enrollmentService.getEnrollmentsCount(studentId);
        JOptionPane.showMessageDialog(null, "已报名活动数量: " + count);
    }
}