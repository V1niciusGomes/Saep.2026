package view;

import java.awt.BorderLayout;
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

import dao.PecaDAO;
import model.Peca;

/**
 * PecaView — tela de cadastro de peça/estoque (molde Item: CRUD simples).
 * Mostra campos numéricos (preco, estoque) com validação na gravação.
 */
public class PecaView extends JFrame {

    private final PecaDAO dao = new PecaDAO();

    private final JTextField campoBusca = new JTextField(16);
    private final JTextField campoNome = new JTextField();
    private final JTextField campoCodigo = new JTextField();
    private final JTextField campoPreco = new JTextField();
    private final JTextField campoEstoque = new JTextField();

    private int idSelecionado = 0;

    private final DefaultTableModel modelo = new DefaultTableModel(
            new String[] { "ID", "Nome", "Código", "Preço", "Estoque" }, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable tabela = new JTable(modelo);

    public PecaView() {
        setTitle("Peças (estoque)");
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
        form.setBorder(BorderFactory.createTitledBorder("Dados da peça"));
        form.add(new JLabel("Nome"));     form.add(campoNome);
        form.add(new JLabel("Código"));   form.add(campoCodigo);
        form.add(new JLabel("Preço"));    form.add(campoPreco);
        form.add(new JLabel("Estoque"));  form.add(campoEstoque);

        JButton btnNovo = new JButton("Novo");
        JButton btnSalvar = new JButton("Salvar");
        JButton btnExcluir = new JButton("Excluir");
        btnNovo.addActionListener(e -> limpar());
        btnSalvar.addActionListener(e -> salvar());
        btnExcluir.addActionListener(e -> excluir());
        JPanel acoes = new JPanel(new GridLayout(3, 1, 8, 8));
        acoes.add(btnNovo); acoes.add(btnSalvar); acoes.add(btnExcluir);

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
        List<Peca> lista = dao.listar(campoBusca.getText());
        for (Peca p : lista) {
            modelo.addRow(new Object[] { p.getId(), p.getNome(), p.getCodigo(), p.getPreco(), p.getEstoque() });
        }
        limpar();
    }

    private void preencherSelecionado() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) return;
        idSelecionado = (int) modelo.getValueAt(linha, 0);
        campoNome.setText(texto(modelo.getValueAt(linha, 1)));
        campoCodigo.setText(texto(modelo.getValueAt(linha, 2)));
        campoPreco.setText(texto(modelo.getValueAt(linha, 3)));
        campoEstoque.setText(texto(modelo.getValueAt(linha, 4)));
    }

    private void salvar() {
        String nome = campoNome.getText().trim();
        if (nome.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nome é obrigatório.");
            return;
        }
        double preco = 0;
        int estoque = 0;
        try {
            String v = campoPreco.getText().trim().replace(",", ".");
            if (!v.isEmpty()) preco = Double.parseDouble(v);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Preço deve ser um número (ex.: 180.00).");
            return;
        }
        try {
            String v = campoEstoque.getText().trim();
            if (!v.isEmpty()) estoque = Integer.parseInt(v);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Estoque deve ser um número inteiro.");
            return;
        }
        Peca p = new Peca();
        p.setId(idSelecionado);
        p.setNome(nome);
        p.setCodigo(campoCodigo.getText().trim());
        p.setPreco(preco);
        p.setEstoque(estoque);
        try {
            if (idSelecionado > 0) dao.atualizar(p);
            else dao.inserir(p);
            carregar();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Não gravou.\n" + ex.getMessage());
        }
    }

    private void excluir() {
        if (idSelecionado <= 0) {
            JOptionPane.showMessageDialog(this, "Selecione uma peça na tabela.");
            return;
        }
        int ok = JOptionPane.showConfirmDialog(this, "Excluir a peça selecionada?", "Confirmar",
                JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) return;
        try {
            dao.excluir(idSelecionado);
            carregar();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Não excluiu.\n" + ex.getMessage()
                    + "\n(Peça usada em alguma OS não pode ser excluída.)");
        }
    }

    private void limpar() {
        idSelecionado = 0;
        tabela.clearSelection();
        campoNome.setText("");
        campoCodigo.setText("");
        campoPreco.setText("");
        campoEstoque.setText("");
    }

    private String texto(Object o) { return o == null ? "" : o.toString(); }
}
