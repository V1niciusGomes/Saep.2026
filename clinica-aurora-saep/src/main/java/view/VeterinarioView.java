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

import dao.VeterinarioDAO;
import model.Veterinario;

/**
 * VeterinarioView — tela de CRUD simples (sem relacionamentos).
 * Segue o MESMO molde do TutorView; veja lá os comentários detalhados do
 * fluxo (carregar / preencherSelecionado / salvar / excluir / limpar).
 *
 * Diferença: aqui os campos são nome, CRMV e especialidade. O CRMV é único
 * no banco, então um valor repetido faz o DAO cair no catch (tratado abaixo).
 *
 * [MOLDE] Esta é a cópia mais "enxuta" para começar um cadastro novo.
 */
public class VeterinarioView extends JFrame {

    private final VeterinarioDAO dao = new VeterinarioDAO();
    private final JTextField campoBusca = new JTextField(16);
    private final JTextField campoNome = new JTextField();
    private final JTextField campoCrmv = new JTextField();
    private final JTextField campoEspecialidade = new JTextField();
    private int idSelecionado = 0; // 0 = inserir; >0 = atualizar

    private final DefaultTableModel modelo = new DefaultTableModel(
            new String[] { "ID", "Nome", "CRMV", "Especialidade" }, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable tabela = new JTable(modelo);

    public VeterinarioView() {
        setTitle("Veterinários");
        setSize(760, 520);
        setLocationRelativeTo(null);

        // NORTE: busca
        JPanel busca = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.addActionListener(e -> carregar());
        busca.add(new JLabel("Nome"));
        busca.add(campoBusca);
        busca.add(btnBuscar);

        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.getSelectionModel().addListSelectionListener(e -> preencherSelecionado());

        // SUL: formulário
        JPanel form = new JPanel(new GridLayout(3, 2, 8, 8));
        form.setBorder(BorderFactory.createTitledBorder("Dados do veterinário"));
        form.add(new JLabel("Nome"));
        form.add(campoNome);
        form.add(new JLabel("CRMV"));
        form.add(campoCrmv);
        form.add(new JLabel("Especialidade"));
        form.add(campoEspecialidade);

        // SUL: ações
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

        JLabel faixa = new JLabel("  CRMV é único no banco. Se repetir, o MySQL recusa.");
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

    /** READ — carrega a tabela com os veterinários. */
    private void carregar() {
        modelo.setRowCount(0);
        List<Veterinario> lista = dao.listar(campoBusca.getText());
        for (Veterinario v : lista) {
            modelo.addRow(new Object[] { v.getId(), v.getNome(), v.getCrmv(), v.getEspecialidade() });
        }
        limpar();
    }

    /** Copia a linha clicada para o formulário. */
    private void preencherSelecionado() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) return;
        idSelecionado = (int) modelo.getValueAt(linha, 0);
        campoNome.setText(texto(modelo.getValueAt(linha, 1)));
        campoCrmv.setText(texto(modelo.getValueAt(linha, 2)));
        campoEspecialidade.setText(texto(modelo.getValueAt(linha, 3)));
    }

    /** CREATE/UPDATE — valida obrigatórios e grava. */
    private void salvar() {
        String nome = campoNome.getText().trim();
        String crmv = campoCrmv.getText().trim();
        if (nome.isEmpty() || crmv.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nome e CRMV são obrigatórios.");
            return;
        }
        Veterinario v = new Veterinario();
        v.setId(idSelecionado);
        v.setNome(nome);
        v.setCrmv(crmv);
        v.setEspecialidade(campoEspecialidade.getText().trim());
        try {
            if (idSelecionado > 0) {
                dao.atualizar(v);
            } else {
                dao.inserir(v);
            }
            carregar();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Não gravou.\n" + ex.getMessage()
                    + "\n(CRMV repetido? Ele precisa ser único.)");
        }
    }

    /** DELETE — remove o selecionado, com confirmação. */
    private void excluir() {
        if (idSelecionado <= 0) {
            JOptionPane.showMessageDialog(this, "Selecione um veterinário na tabela.");
            return;
        }
        int ok = JOptionPane.showConfirmDialog(this, "Excluir o veterinário selecionado?", "Confirmar",
                JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) return;
        try {
            dao.excluir(idSelecionado);
            carregar();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Não excluiu.\n" + ex.getMessage()
                    + "\n(Veterinário com consultas não pode ser excluído.)");
        }
    }

    /** Zera seleção e campos. */
    private void limpar() {
        idSelecionado = 0;
        tabela.clearSelection();
        campoNome.setText("");
        campoCrmv.setText("");
        campoEspecialidade.setText("");
    }

    private String texto(Object o) {
        return o == null ? "" : o.toString();
    }
}
