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
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import dao.TutorDAO;
import model.Tutor;

public class TutorView extends JFrame {

    private final TutorDAO dao = new TutorDAO();
    private final JTextField campoBusca = new JTextField(16);
    private final JTextField campoNome = new JTextField();
    private final JTextField campoTelefone = new JTextField();
    private final JTextField campoEmail = new JTextField();
    private final JTextField campoCpf = new JTextField();
    private int idSelecionado = 0;
    private final DefaultTableModel modelo = new DefaultTableModel(
            new String[] { "ID", "Nome", "Telefone", "E-mail", "CPF" }, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable tabela = new JTable(modelo);

    public TutorView() {
        setTitle("Tutores");
        setSize(760, 520);
        setLocationRelativeTo(null);

        JPanel busca = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.addActionListener(e -> carregar());
        busca.add(new JLabel("Nome"));
        busca.add(campoBusca);
        busca.add(btnBuscar);

        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.getSelectionModel().addListSelectionListener(e -> preencherSelecionado());

        JPanel form = new JPanel(new GridLayout(4, 2, 8, 8));
        form.setBorder(BorderFactory.createTitledBorder("Dados do tutor"));
        form.add(new JLabel("Nome"));
        form.add(campoNome);
        form.add(new JLabel("Telefone"));
        form.add(campoTelefone);
        form.add(new JLabel("E-mail"));
        form.add(campoEmail);
        form.add(new JLabel("CPF"));
        form.add(campoCpf);

        JButton btnNovo = new JButton("Novo");
        JButton btnSalvar = new JButton("Salvar");
        JButton btnExcluir = new JButton("Excluir");
        btnNovo.addActionListener(e -> limpar());
        btnSalvar.addActionListener(e -> salvar());
        btnExcluir.addActionListener(e -> excluir());
        JPanel acoes = new JPanel(new GridLayout(3, 1, 8, 8));
        acoes.add(btnNovo);
        acoes.add(btnSalvar);
        acoes.add(btnExcluir);

        JLabel faixa = new JLabel("  ID é gerado pelo MySQL (AUTO_INCREMENT). CPF é guardado criptografado (AES).");
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
            modelo.addRow(new Object[] { t.getId(), t.getNome(), t.getTelefone(), t.getEmail(), t.getCpf() });
        }
        limpar();
    }

    private void preencherSelecionado() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) return;
        idSelecionado = (int) modelo.getValueAt(linha, 0);
        campoNome.setText(texto(modelo.getValueAt(linha, 1)));
        campoTelefone.setText(texto(modelo.getValueAt(linha, 2)));
        campoEmail.setText(texto(modelo.getValueAt(linha, 3)));
        campoCpf.setText(texto(modelo.getValueAt(linha, 4)));
    }

    private void salvar() {
        String nome = campoNome.getText().trim();
        if (nome.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nome é obrigatório.");
            return;
        }
        Tutor t = new Tutor();
        t.setId(idSelecionado);
        t.setNome(nome);
        t.setTelefone(campoTelefone.getText().trim());
        t.setEmail(campoEmail.getText().trim());
        t.setCpf(campoCpf.getText().trim());
        try {
            if (idSelecionado > 0) {
                dao.atualizar(t);
            } else {
                dao.inserir(t);
            }
            carregar();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Não gravou.\n" + ex.getMessage());
        }
    }

    private void excluir() {
        if (idSelecionado <= 0) {
            JOptionPane.showMessageDialog(this, "Selecione um tutor na tabela.");
            return;
        }
        int ok = JOptionPane.showConfirmDialog(this, "Excluir o tutor selecionado?", "Confirmar",
                JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) return;
        try {
            dao.excluir(idSelecionado);
            carregar();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Não excluiu.\n" + ex.getMessage()
                    + "\n(Tutor com pets cadastrados não pode ser excluído.)");
        }
    }

    private void limpar() {
        idSelecionado = 0;
        tabela.clearSelection();
        campoNome.setText("");
        campoTelefone.setText("");
        campoEmail.setText("");
        campoCpf.setText("");
    }

    private String texto(Object o) {
        return o == null ? "" : o.toString();
    }
}
