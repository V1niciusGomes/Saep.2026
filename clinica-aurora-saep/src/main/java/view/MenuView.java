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
 * MenuView — tela de menu principal, aberta logo após o login.
 *
 * PAPEL NA ARQUITETURA (camada view):
 *   É o "centro de navegação". Cada botão abre a tela (view) de um cadastro.
 *   Não tem SQL nem regra: só monta botões e manda abrir outras telas.
 *
 * Recebe o Usuario logado no construtor, só para dar as boas-vindas pelo nome.
 *
 * [MOLDE] Para adicionar um cadastro novo ao sistema:
 *   1. crie um botão (copie uma linha "JButton btnX = new JButton(...)");
 *   2. ligue a ação dele à sua tela (btnX.addActionListener(e -> new SuaView()...));
 *   3. aumente o GridLayout e dê um botoes.add(btnX).
 */
public class MenuView extends JFrame {

    public MenuView(Usuario usuario) {
        setTitle("Clínica Vet Aurora — Menu");
        // Fechar esta janela encerra o programa (é a janela "principal").
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(480, 420);
        setLocationRelativeTo(null); // centraliza na tela

        // ----- Faixa azul do topo (cabeçalho visual) -----
        JPanel topo = new JPanel(new BorderLayout());
        topo.setBackground(new Color(22, 65, 148)); // [MOLDE] cor da identidade visual
        topo.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        JLabel titulo = new JLabel("Olá, " + usuario.getNome());
        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 18));
        JLabel sub = new JLabel("Semana 2 — cadastros da clínica");
        sub.setForeground(new Color(200, 220, 245));
        topo.add(titulo, BorderLayout.NORTH);
        topo.add(sub, BorderLayout.SOUTH);

        // ----- Botões do menu (um por cadastro) -----
        JButton btnTutores = new JButton("Tutores");
        JButton btnPets = new JButton("Pets");
        JButton btnVets = new JButton("Veterinários");
        JButton btnConsultas = new JButton("Consultas");
        JButton btnSair = new JButton("Sair");

        // Cada botão abre a tela correspondente. "new XView().setVisible(true)"
        // cria e mostra a janela do cadastro.
        btnTutores.addActionListener(e -> new TutorView().setVisible(true));
        btnPets.addActionListener(e -> new PetView().setVisible(true));
        btnVets.addActionListener(e -> new VeterinarioView().setVisible(true));
        btnConsultas.addActionListener(e -> new ConsultaView().setVisible(true));
        // Sair: volta ao login e fecha o menu atual.
        btnSair.addActionListener(e -> {
            new LoginView().setVisible(true);
            dispose(); // fecha esta janela de menu
        });

        // GridLayout(5,1): empilha os 5 botões em uma coluna.
        // [MOLDE] se adicionar botões, ajuste o número de linhas aqui.
        JPanel botoes = new JPanel(new GridLayout(5, 1, 10, 10));
        botoes.setBorder(BorderFactory.createEmptyBorder(24, 40, 24, 40));
        botoes.add(btnTutores);
        botoes.add(btnPets);
        botoes.add(btnVets);
        botoes.add(btnConsultas);
        botoes.add(btnSair);

        // Monta a janela: topo no NORTE, botões no CENTRO.
        add(topo, BorderLayout.NORTH);
        add(botoes, BorderLayout.CENTER);
    }
}
