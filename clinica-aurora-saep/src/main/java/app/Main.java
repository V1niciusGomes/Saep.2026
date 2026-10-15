package app;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import view.LoginView;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // Look and feel
            }
            new LoginView().setVisible(true);
        });
    }
}
