package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import dao.UsuarioDAO;
import model.Usuario;

/**
 * LoginView — primeira tela: pede usuário e senha.
 *
 * PAPEL NA ARQUITETURA (camada view):
 *   A tela NÃO conversa com o banco direto. Ela chama o UsuarioDAO, que faz
 *   o trabalho de verificar as credenciais. Separar assim (tela x acesso a
 *   dados) é o coração do padrão deste projeto.
 *
 * Fluxo: digita -> clica Entrar -> DAO verifica -> se ok, abre o MenuView.
 *
 * [MOLDE] Tela de login é genérica; costuma ficar igual entre projetos.
 *         Troque só os textos do título (identidade do seu sistema).
 */
public class LoginView extends JFrame {

    // Campos de entrada. JPasswordField esconde a senha com bolinhas.
    private final JTextField campoLogin = new JTextField(16);
    private final JPasswordField campoSenha = new JPasswordField(16);

    public LoginView() {
        setTitle("Projeto Base — Login");                // [MOLDE] título do seu sistema
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  // fechar aqui encerra o app
        setSize(460, 320);
        setLocationRelativeTo(null); // centraliza
        setResizable(false);         // tamanho fixo

        // ----- Cabeçalho colorido -----
        JPanel topo = new JPanel(new BorderLayout());
        topo.setBackground(new Color(22, 65, 148));      // [MOLDE] cor da identidade visual
        topo.setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));
        JLabel titulo = new JLabel("Projeto Base");      // [MOLDE] nome do sistema
        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 18));
        JLabel sub = new JLabel("Entre com seu usuário");
        sub.setForeground(new Color(200, 220, 245));
        topo.add(titulo, BorderLayout.NORTH);
        topo.add(sub, BorderLayout.SOUTH);

        // ----- Formulário (GridBagLayout = posiciona por linha/coluna) -----
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 6, 6, 6);     // respiro entre os componentes
        g.anchor = GridBagConstraints.WEST;    // alinha à esquerda

        g.gridx = 0; g.gridy = 0;              // coluna 0, linha 0
        form.add(new JLabel("Usuário"), g);
        g.gridx = 1;                           // coluna 1, mesma linha
        form.add(campoLogin, g);

        g.gridx = 0; g.gridy = 1;              // próxima linha
        form.add(new JLabel("Senha"), g);
        g.gridx = 1;
        form.add(campoSenha, g);

        JButton entrar = new JButton("Entrar");
        entrar.addActionListener(e -> tentarLogin());     // clique chama tentarLogin()
        getRootPane().setDefaultButton(entrar);           // Enter também aciona o botão

        g.gridx = 1; g.gridy = 2;
        g.anchor = GridBagConstraints.EAST;
        form.add(entrar, g);

        // Dica fixa com as credenciais de teste.
        JLabel dica = new JLabel("Teste: admin  /  123456");
        dica.setForeground(new Color(90, 115, 148));
        dica.setBorder(BorderFactory.createEmptyBorder(0, 20, 12, 20));

        add(topo, BorderLayout.NORTH);
        add(form, BorderLayout.CENTER);
        add(dica, BorderLayout.SOUTH);

        campoLogin.setText("admin"); // já vem preenchido para agilizar o teste
    }

    /** Lê os campos, valida e pede ao DAO para autenticar. */
    private void tentarLogin() {
        String login = campoLogin.getText().trim();
        String senha = new String(campoSenha.getPassword());

        // Validação básica de tela: não deixa campos vazios irem ao banco.
        if (login.isEmpty() || senha.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Preencha usuário e senha.");
            return;
        }
        try {
            // O DAO devolve o Usuario se as credenciais baterem, ou null se não.
            Usuario u = new UsuarioDAO().autenticar(login, senha);
            if (u == null) {
                JOptionPane.showMessageDialog(this, "Usuário ou senha inválidos.");
                campoSenha.setText("");
                campoSenha.requestFocus();
                return;
            }
            // Deu certo: abre o menu passando o usuário e fecha o login.
            new MenuView(u).setVisible(true);
            dispose();
        } catch (Exception ex) {
            // Erro de conexão (serviço fora do ar, senha errada no ConnectionFactory...).
            JOptionPane.showMessageDialog(this,
                "Não conectou no MySQL.\nConfira se o serviço está no ar e a senha em ConnectionFactory.\n\n"
                + ex.getMessage());
        }
    }
}
