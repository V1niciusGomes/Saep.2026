package app;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import view.LoginView;

/**
 * Main — ponto de entrada. Ajusta a aparência e abre a tela de login.
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }
            new LoginView().setVisible(true);
        });
    }
}
