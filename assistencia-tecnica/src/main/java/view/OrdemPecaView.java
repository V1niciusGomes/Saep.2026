package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import dao.OrdemPecaDAO;
import dao.OrdemServicoDAO;
import dao.PecaDAO;
import model.OrdemPeca;
import model.OrdemServico;
import model.Peca;

/**
 * OrdemPecaView — peças usadas em cada OS (molde ItemLigacao / muitos-para-muitos).
 * Combos: a OS (mostrada como "OS #id - cliente") e a peça; campo próprio: quantidade.
 */
public class OrdemPecaView extends JFrame {

    private final OrdemPecaDAO dao = new OrdemPecaDAO();
    private final OrdemServicoDAO osDao = new OrdemServicoDAO();
    private final PecaDAO pecaDao = new PecaDAO();

    private final JTextField campoBusca = new JTextField(16);
    private final JComboBox<OrdemServico> comboOrdem = new JComboBox<>();
    private final JComboBox<Peca> comboPeca = new JComboBox<>();
    private final JTextField campoQtd = new JTextField();

    private int idSelecionado = 0;

    private final DefaultTableModel modelo = new DefaultTableModel(
            new String[] { "ID", "Ordem de Serviço", "Peça", "Quantidade" }, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable tabela = new JTable(modelo);

    public OrdemPecaView() {
        setTitle("Peças usadas nas OS");
        setSize(860, 540);
        setLocationRelativeTo(null);

        JPanel busca = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.addActionListener(e -> carregar());
        busca.add(new JLabel("Peça/cliente"));
        busca.add(campoBusca);
        busca.add(btnBuscar);

        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.getSelectionModel().addListSelectionListener(e -> preencherSelecionado());

        comboOrdem.setRenderer(new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(JList<?> l, Object v, int i, boolean s, boolean f) {
                super.getListCellRendererComponent(l, v, i, s, f);
                if (v instanceof OrdemServico o) setText("OS #" + o.getId() + " - " + o.getNomeCliente());
                return this;
            }
        });
        comboPeca.setRenderer(new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(JList<?> l, Object v, int i, boolean s, boolean f) {
                super.getListCellRendererComponent(l, v, i, s, f);
                if (v instanceof Peca p) setText(p.getNome() + " (" + p.getCodigo() + ")");
                return this;
            }
        });

        JPanel form = new JPanel(new GridLayout(3, 2, 8, 8));
        form.setBorder(BorderFactory.createTitledBorder("Peça usada na OS"));
        form.add(new JLabel("Ordem de Serviço")); form.add(comboOrdem);
        form.add(new JLabel("Peça"));             form.add(comboPeca);
        form.add(new JLabel("Quantidade"));       form.add(campoQtd);

        JButton btnNovo = new JButton("Novo");
        JButton btnSalvar = new JButton("Salvar");
        JButton btnExcluir = new JButton("Excluir");
        btnNovo.addActionListener(e -> limpar());
        btnSalvar.addActionListener(e -> salvar());
        btnExcluir.addActionListener(e -> excluir());
        JPanel acoes = new JPanel(new GridLayout(3, 1, 8, 8));
        acoes.add(btnNovo); acoes.add(btnSalvar); acoes.add(btnExcluir);

        JLabel faixa = new JLabel("  Cada linha indica uma peça usada em uma OS. Precisa haver OS e peça cadastradas.");
        faixa.setOpaque(true);
        faixa.setBackground(new Color(226, 243, 236));
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
        carregarCombos();
        carregar();
    }

    private void carregarCombos() {
        DefaultComboBoxModel<OrdemServico> mo = new DefaultComboBoxModel<>();
        for (OrdemServico o : osDao.listar("")) mo.addElement(o);
        comboOrdem.setModel(mo);

        DefaultComboBoxModel<Peca> mp = new DefaultComboBoxModel<>();
        for (Peca p : pecaDao.listar("")) mp.addElement(p);
        comboPeca.setModel(mp);
    }

    private void carregar() {
        modelo.setRowCount(0);
        List<OrdemPeca> lista = dao.listar(campoBusca.getText());
        for (OrdemPeca op : lista) {
            modelo.addRow(new Object[] { op.getId(), op.getDescricaoOrdem(), op.getNomePeca(), op.getQuantidade() });
        }
        limpar();
    }

    private void preencherSelecionado() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) return;
        idSelecionado = (int) modelo.getValueAt(linha, 0);
        String descOrdem = texto(modelo.getValueAt(linha, 1));
        for (int k = 0; k < comboOrdem.getItemCount(); k++) {
            OrdemServico o = comboOrdem.getItemAt(k);
            if (descOrdem.startsWith("OS #" + o.getId() + " ")) { comboOrdem.setSelectedIndex(k); break; }
        }
        String nomePeca = texto(modelo.getValueAt(linha, 2));
        for (int k = 0; k < comboPeca.getItemCount(); k++) {
            if (comboPeca.getItemAt(k).getNome().equals(nomePeca)) { comboPeca.setSelectedIndex(k); break; }
        }
        campoQtd.setText(texto(modelo.getValueAt(linha, 3)));
    }

    private void salvar() {
        OrdemServico ordem = (OrdemServico) comboOrdem.getSelectedItem();
        Peca peca = (Peca) comboPeca.getSelectedItem();
        if (ordem == null || peca == null) {
            JOptionPane.showMessageDialog(this, "Selecione a OS e a peça. Cadastre-as antes se faltar.");
            return;
        }
        int qtd = 1;
        try {
            String v = campoQtd.getText().trim();
            if (!v.isEmpty()) qtd = Integer.parseInt(v);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Quantidade deve ser um número inteiro.");
            return;
        }
        if (qtd <= 0) {
            JOptionPane.showMessageDialog(this, "Quantidade deve ser maior que zero.");
            return;
        }

        OrdemPeca op = new OrdemPeca();
        op.setId(idSelecionado);
        op.setIdOrdemServico(ordem.getId());
        op.setIdPeca(peca.getId());
        op.setQuantidade(qtd);
        try {
            if (idSelecionado > 0) dao.atualizar(op);
            else dao.inserir(op);
            carregar();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Não gravou.\n" + ex.getMessage());
        }
    }

    private void excluir() {
        if (idSelecionado <= 0) {
            JOptionPane.showMessageDialog(this, "Selecione uma linha na tabela.");
            return;
        }
        int ok = JOptionPane.showConfirmDialog(this, "Remover esta peça da OS?", "Confirmar",
                JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) return;
        try {
            dao.excluir(idSelecionado);
            carregar();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Não excluiu.\n" + ex.getMessage());
        }
    }

    private void limpar() {
        idSelecionado = 0;
        tabela.clearSelection();
        if (comboOrdem.getItemCount() > 0) comboOrdem.setSelectedIndex(0);
        if (comboPeca.getItemCount() > 0) comboPeca.setSelectedIndex(0);
        campoQtd.setText("");
    }

    private String texto(Object o) { return o == null ? "" : o.toString(); }
}
