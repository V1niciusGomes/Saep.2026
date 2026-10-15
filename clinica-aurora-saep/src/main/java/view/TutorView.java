package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import dao.TutorDAO;
import model.Tutor;

public class TutorView extends JFrame {

    private final TutorDAO dao = new TutorDAO();
    private final JTextField campoBusca = new JTextField(16);
    private final JTextField campoNome = new JTextField();
    private final JTextField campoTelefone = new JTextField();
    private final JTextField campoEmail = new JTextField();
    private final DefaultTableModel modelo = new DefaultTableModel(
            new String[] { "ID", "Nome", "Telefone", "E-mail" }, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable tabela = new JTable(modelo);

    public TutorView() {
        setTitle("Tutores");
        setSize(720, 480);
        setLocationRelativeTo(null);

        JPanel busca = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.addActionListener(e -> carregar());
        busca.add(new JLabel("Nome"));
        busca.add(campoBusca);
        busca.add(btnBuscar);

        JPanel form = new JPanel(new GridLayout(3, 2, 8, 8));
        form.setBorder(BorderFactory.createTitledBorder("Novo tutor"));
        form.add(new JLabel("Nome"));
        form.add(campoNome);
        form.add(new JLabel("Telefone"));
        form.add(campoTelefone);
        form.add(new JLabel("E-mail"));
        form.add(campoEmail);

        JButton btnSalvar = new JButton("Salvar");
        btnSalvar.addActionListener(e -> salvar());
        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        acoes.add(btnSalvar);

        JLabel faixa = new JLabel("  ID é gerado pelo MySQL (AUTO_INCREMENT). Não se digita.");
        faixa.setOpaque(true);
        faixa.setBackground(new Color(232, 241, 248));
        faixa.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JPanel sul = new JPanel(new BorderLayout(8, 8));
        sul.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        sul.add(form, BorderLayout.CENTER);
        sul.add(acoes, BorderLayout.EAST);
        sul.add(faixa, BorderLayout.SOUTH);

        add(busca, BorderLayout.NORTH);
        add(new JScrollPane(tabela), BorderLayout.CENTER);
        add(sul, BorderLayout.SOUTH);

        getRootPane().setDefaultButton(btnBuscar);
        carregar();
    }

    private void carregar() {
        modelo.setRowCount(0);
        List<Tutor> lista = dao.listar(campoBusca.getText());
        for (Tutor t : lista) {
            modelo.addRow(new Object[] { t.getId(), t.getNome(), t.getTelefone(), t.getEmail() });
        }
    }

    private void salvar() {
        String nome = campoNome.getText().trim();
        if (nome.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nome é obrigatório.");
            return;
        }
        Tutor t = new Tutor();
        t.setNome(nome);
        t.setTelefone(campoTelefone.getText().trim());
        t.setEmail(campoEmail.getText().trim());
        try {
            dao.inserir(t);
            campoNome.setText("");
            campoTelefone.setText("");
            campoEmail.setText("");
            carregar();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Não gravou.
" + ex.getMessage());
        }
    }
}
