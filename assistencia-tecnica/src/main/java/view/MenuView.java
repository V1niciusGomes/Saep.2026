package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import model.Usuario;

/**
 * MenuView — centro de navegação. Cada botão abre a tela de um cadastro.
 */
public class MenuView extends JFrame {

    public MenuView(Usuario usuario) {
        setTitle("Assistência Técnica — Menu");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(520, 500);
        setLocationRelativeTo(null);

        JPanel topo = new JPanel(new BorderLayout());
        topo.setBackground(new Color(30, 110, 90));
        topo.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        JLabel titulo = new JLabel("Olá, " + usuario.getNome());
        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 18));
        JLabel sub = new JLabel("Menu principal");
        sub.setForeground(new Color(205, 235, 225));
        topo.add(titulo, BorderLayout.NORTH);
        topo.add(sub, BorderLayout.SOUTH);

        JButton btnClientes = new JButton("Clientes");
        JButton btnTecnicos = new JButton("Técnicos");
        JButton btnPecas = new JButton("Peças (estoque)");
        JButton btnAparelhos = new JButton("Aparelhos (de um cliente)");
        JButton btnOrdens = new JButton("Ordens de Serviço");
        JButton btnOrdemPecas = new JButton("Peças usadas nas OS");
        JButton btnSair = new JButton("Sair");

        btnClientes.addActionListener(e -> new ClienteView().setVisible(true));
        btnTecnicos.addActionListener(e -> new TecnicoView().setVisible(true));
        btnPecas.addActionListener(e -> new PecaView().setVisible(true));
        btnAparelhos.addActionListener(e -> new AparelhoView().setVisible(true));
        btnOrdens.addActionListener(e -> new OrdemServicoView().setVisible(true));
        btnOrdemPecas.addActionListener(e -> new OrdemPecaView().setVisible(true));
        btnSair.addActionListener(e -> {
            new LoginView().setVisible(true);
            dispose();
        });

        JPanel botoes = new JPanel(new GridLayout(7, 1, 10, 10));
        botoes.setBorder(BorderFactory.createEmptyBorder(24, 40, 24, 40));
        botoes.add(btnClientes);
        botoes.add(btnTecnicos);
        botoes.add(btnPecas);
        botoes.add(btnAparelhos);
        botoes.add(btnOrdens);
        botoes.add(btnOrdemPecas);
        botoes.add(btnSair);

        add(topo, BorderLayout.NORTH);
        add(botoes, BorderLayout.CENTER);
    }
}
