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

public class MenuView extends JFrame {

    public MenuView(Usuario usuario) {
        setTitle("Clínica Vet Aurora — Menu");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(480, 420);
        setLocationRelativeTo(null);

        JPanel topo = new JPanel(new BorderLayout());
        topo.setBackground(new Color(22, 65, 148));
        topo.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        JLabel titulo = new JLabel("Olá, " + usuario.getNome());
        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 18));
        JLabel sub = new JLabel("Semana 2 — cadastros da clínica");
        sub.setForeground(new Color(200, 220, 245));
        topo.add(titulo, BorderLayout.NORTH);
        topo.add(sub, BorderLayout.SOUTH);

        JButton btnTutores = new JButton("Tutores");
        JButton btnPets = new JButton("Pets");
        JButton btnVets = new JButton("Veterinários");
        JButton btnConsultas = new JButton("Consultas");
        JButton btnSair = new JButton("Sair");

        btnTutores.addActionListener(e -> new TutorView().setVisible(true));
        btnPets.addActionListener(e -> new PetView().setVisible(true));
        btnVets.addActionListener(e -> new VeterinarioView().setVisible(true));
        btnConsultas.addActionListener(e -> new ConsultaView().setVisible(true));
        btnSair.addActionListener(e -> {
            new LoginView().setVisible(true);
            dispose();
        });

        JPanel botoes = new JPanel(new GridLayout(5, 1, 10, 10));
        botoes.setBorder(BorderFactory.createEmptyBorder(24, 40, 24, 40));
        botoes.add(btnTutores);
        botoes.add(btnPets);
        botoes.add(btnVets);
        botoes.add(btnConsultas);
        botoes.add(btnSair);

        add(topo, BorderLayout.NORTH);
        add(botoes, BorderLayout.CENTER);
    }
}
