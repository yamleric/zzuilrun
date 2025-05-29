import view.LoginFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // 使用SwingUtilities确保线程安全
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}