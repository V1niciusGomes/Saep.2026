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

import dao.ItemBDAO;
import model.ItemB;

/**
 * ItemBView — tela de cadastro simples do segundo cadastro (ItemB).
 * É o MESMO molde da ItemView, só com outra entidade. Serve para mostrar como
 * o molde de tela CRUD se repete sem mistério.
 *
 * [MOLDE] Mesmas instruções da ItemView: copie e troque entidade/DAO/campos.
 */
public class ItemBView extends JFrame {

    private final ItemBDAO dao = new ItemBDAO();

    private final JTextField campoBusca = new JTextField(16);
    private final JTextField campoNome = new JTextField();
    private final JTextField campoDescricao = new JTextField();

    private int idSelecionado = 0;

    private final DefaultTableModel modelo = new DefaultTableModel(
            new String[] { "ID", "Nome", "Descrição" }, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable tabela = new JTable(modelo);

    public ItemBView() {
        setTitle("Itens B");
        setSize(700, 480);
        setLocationRelativeTo(null);

        // ----- NORTE: busca -----
        JPanel busca = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.addActionListener(e -> carregar());
        busca.add(new JLabel("Nome"));
        busca.add(campoBusca);
        busca.add(btnBuscar);

        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.getSelectionModel().addListSelectionListener(e -> preencherSelecionado());

        // ----- SUL: formulário -----
        JPanel form = new JPanel(new GridLayout(2, 2, 8, 8));
        form.setBorder(BorderFactory.createTitledBorder("Dados do item B"));
        form.add(new JLabel("Nome"));
        form.add(campoNome);
        form.add(new JLabel("Descrição"));
        form.add(campoDescricao);

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

        JPanel sul = new JPanel(new BorderLayout(8, 8));
        sul.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        sul.add(form, BorderLayout.CENTER);
        sul.add(acoes, BorderLayout.EAST);

        add(busca, BorderLayout.NORTH);
        add(new JScrollPane(tabela), BorderLayout.CENTER);
        add(sul, BorderLayout.SOUTH);

        getRootPane().setDefaultButton(btnBuscar);
        carregar();
    }

    private void carregar() {
        modelo.setRowCount(0);
        List<ItemB> lista = dao.listar(campoBusca.getText());
        for (ItemB b : lista) {
            modelo.addRow(new Object[] { b.getId(), b.getNome(), b.getDescricao() });
        }
        limpar();
    }

    private void preencherSelecionado() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) return;
        idSelecionado = (int) modelo.getValueAt(linha, 0);
        campoNome.setText(texto(modelo.getValueAt(linha, 1)));
        campoDescricao.setText(texto(modelo.getValueAt(linha, 2)));
    }

    private void salvar() {
        String nome = campoNome.getText().trim();
        if (nome.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nome é obrigatório.");
            return;
        }
        ItemB b = new ItemB();
        b.setId(idSelecionado);
        b.setNome(nome);
        b.setDescricao(campoDescricao.getText().trim());
        try {
            if (idSelecionado > 0) dao.atualizar(b);
            else dao.inserir(b);
            carregar();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Não gravou.\n" + ex.getMessage());
        }
    }

    private void excluir() {
        if (idSelecionado <= 0) {
            JOptionPane.showMessageDialog(this, "Selecione um item B na tabela.");
            return;
        }
        int ok = JOptionPane.showConfirmDialog(this, "Excluir o item B selecionado?", "Confirmar",
                JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) return;
        try {
            dao.excluir(idSelecionado);
            carregar();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Não excluiu.\n" + ex.getMessage()
                    + "\n(Item B usado em alguma ligação não pode ser excluído.)");
        }
    }

    private void limpar() {
        idSelecionado = 0;
        tabela.clearSelection();
        campoNome.setText("");
        campoDescricao.setText("");
    }

    private String texto(Object o) {
        return o == null ? "" : o.toString();
    }
}
