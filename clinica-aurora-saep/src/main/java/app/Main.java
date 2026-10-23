package app;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import view.LoginView;

/**
 * Main — ponto de entrada do programa (o método main).
 *
 * PAPEL NA ARQUITETURA (camada app):
 *   É só a "ignição" do sistema. Não tem regra de negócio nem SQL.
 *   Ele ajusta a aparência e manda abrir a primeira tela (o login).
 *
 * [MOLDE] Normalmente você não mexe aqui. Se a sua primeira tela não for o
 *         login, troque "new LoginView()" pela tela inicial desejada.
 */
public class Main {
    public static void main(String[] args) {
        // SwingUtilities.invokeLater: boa prática do Swing. Garante que a
        // interface seja criada na "thread de eventos" (a que cuida da tela),
        // evitando travamentos e bugs visuais.
        SwingUtilities.invokeLater(() -> {
            try {
                // Faz os botões/janelas usarem a aparência nativa do sistema
                // operacional (Windows, por exemplo), em vez do visual padrão do Java.
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // Se não conseguir, segue com o visual padrão. Não é problema grave.
            }
            // Cria e mostra a tela de login. Daqui em diante, o usuário navega.
            new LoginView().setVisible(true);
        });
    }
}
