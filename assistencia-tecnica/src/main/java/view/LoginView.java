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
 * LoginView — primeira tela. Pede usuário e senha; o DAO verifica.
 */
public class LoginView extends JFrame {

    private final JTextField campoLogin = new JTextField(16);
    private final JPasswordField campoSenha = new JPasswordField(16);

    public LoginView() {
        setTitle("Assistência Técnica — Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(460, 320);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel topo = new JPanel(new BorderLayout());
        topo.setBackground(new Color(30, 110, 90));
        topo.setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));
        JLabel titulo = new JLabel("Assistência Técnica");
        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 18));
        JLabel sub = new JLabel("Gestão de Ordens de Serviço");
        sub.setForeground(new Color(205, 235, 225));
        topo.add(titulo, BorderLayout.NORTH);
        topo.add(sub, BorderLayout.SOUTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 6, 6, 6);
        g.anchor = GridBagConstraints.WEST;

        g.gridx = 0; g.gridy = 0;
        form.add(new JLabel("Usuário"), g);
        g.gridx = 1;
        form.add(campoLogin, g);

        g.gridx = 0; g.gridy = 1;
        form.add(new JLabel("Senha"), g);
        g.gridx = 1;
        form.add(campoSenha, g);

        JButton entrar = new JButton("Entrar");
        entrar.addActionListener(e -> tentarLogin());
        getRootPane().setDefaultButton(entrar);

        g.gridx = 1; g.gridy = 2;
        g.anchor = GridBagConstraints.EAST;
        form.add(entrar, g);

        JLabel dica = new JLabel("Teste: admin  /  123456");
        dica.setForeground(new Color(90, 115, 108));
        dica.setBorder(BorderFactory.createEmptyBorder(0, 20, 12, 20));

        add(topo, BorderLayout.NORTH);
        add(form, BorderLayout.CENTER);
        add(dica, BorderLayout.SOUTH);

        campoLogin.setText("admin");
    }

    private void tentarLogin() {
        String login = campoLogin.getText().trim();
        String senha = new String(campoSenha.getPassword());
        if (login.isEmpty() || senha.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Preencha usuário e senha.");
            return;
        }
        try {
            Usuario u = new UsuarioDAO().autenticar(login, senha);
            if (u == null) {
                JOptionPane.showMessageDialog(this, "Usuário ou senha inválidos.");
                campoSenha.setText("");
                campoSenha.requestFocus();
                return;
            }
            new MenuView(u).setVisible(true);
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Não conectou no MySQL.\nConfira o serviço e a senha em ConnectionFactory.\n\n"
                + ex.getMessage());
        }
    }
}
