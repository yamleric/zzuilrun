import view.LoginFrame;

import javax.swing.*;

import util.DatabaseUtil;

public class Main {
    public static void main(String[] args) {
        // 使用SwingUtilities确保线程安全
        DatabaseUtil.initColleges();
        DatabaseUtil.initSuperAdmin();
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}