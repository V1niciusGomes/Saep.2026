package view;

import java.awt.BorderLayout;
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

import dao.AparelhoDAO;
import dao.ClienteDAO;
import model.Aparelho;
import model.Cliente;

/**
 * AparelhoView — cadastro de aparelho (molde ItemFilho: pertence a um cliente).
 * Tem um JComboBox para escolher o cliente dono.
 */
public class AparelhoView extends JFrame {

    private final AparelhoDAO dao = new AparelhoDAO();
    private final ClienteDAO clienteDao = new ClienteDAO();

    private final JTextField campoBusca = new JTextField(16);
    private final JTextField campoTipo = new JTextField();
    private final JTextField campoMarca = new JTextField();
    private final JTextField campoModelo = new JTextField();
    private final JTextField campoSerie = new JTextField();
    private final JComboBox<Cliente> comboCliente = new JComboBox<>();

    private int idSelecionado = 0;

    private final DefaultTableModel modelo = new DefaultTableModel(
            new String[] { "ID", "Tipo", "Marca", "Modelo", "Nº Série", "Cliente" }, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable tabela = new JTable(modelo);

    public AparelhoView() {
        setTitle("Aparelhos");
        setSize(840, 540);
        setLocationRelativeTo(null);

        JPanel busca = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.addActionListener(e -> carregar());
        busca.add(new JLabel("Tipo/marca/cliente"));
        busca.add(campoBusca);
        busca.add(btnBuscar);

        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.getSelectionModel().addListSelectionListener(e -> preencherSelecionado());

        comboCliente.setRenderer(new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(JList<?> l, Object v, int i, boolean s, boolean f) {
                super.getListCellRendererComponent(l, v, i, s, f);
                if (v instanceof Cliente c) setText(c.getNome());
                return this;
            }
        });

        JPanel form = new JPanel(new GridLayout(5, 2, 8, 8));
        form.setBorder(BorderFactory.createTitledBorder("Dados do aparelho"));
        form.add(new JLabel("Tipo"));       form.add(campoTipo);
        form.add(new JLabel("Marca"));      form.add(campoMarca);
        form.add(new JLabel("Modelo"));     form.add(campoModelo);
        form.add(new JLabel("Nº de Série")); form.add(campoSerie);
        form.add(new JLabel("Cliente (dono)")); form.add(comboCliente);

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
        carregarCombo();
        carregar();
    }

    private void carregarCombo() {
        DefaultComboBoxModel<Cliente> m = new DefaultComboBoxModel<>();
        for (Cliente c : clienteDao.listar("")) m.addElement(c);
        comboCliente.setModel(m);
    }

    private void carregar() {
        modelo.setRowCount(0);
        List<Aparelho> lista = dao.listar(campoBusca.getText());
        for (Aparelho a : lista) {
            modelo.addRow(new Object[] { a.getId(), a.getTipo(), a.getMarca(), a.getModelo(),
                    a.getNumeroSerie(), a.getNomeCliente() });
        }
        limpar();
    }

    private void preencherSelecionado() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) return;
        idSelecionado = (int) modelo.getValueAt(linha, 0);
        campoTipo.setText(texto(modelo.getValueAt(linha, 1)));
        campoMarca.setText(texto(modelo.getValueAt(linha, 2)));
        campoModelo.setText(texto(modelo.getValueAt(linha, 3)));
        campoSerie.setText(texto(modelo.getValueAt(linha, 4)));
        String nomeCliente = texto(modelo.getValueAt(linha, 5));
        for (int k = 0; k < comboCliente.getItemCount(); k++) {
            if (comboCliente.getItemAt(k).getNome().equals(nomeCliente)) {
                comboCliente.setSelectedIndex(k);
                break;
            }
        }
    }

    private void salvar() {
        String tipo = campoTipo.getText().trim();
        if (tipo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tipo é obrigatório.");
            return;
        }
        Cliente dono = (Cliente) comboCliente.getSelectedItem();
        if (dono == null) {
            JOptionPane.showMessageDialog(this, "Cadastre um cliente antes de criar um aparelho.");
            return;
        }
        Aparelho a = new Aparelho();
        a.setId(idSelecionado);
        a.setTipo(tipo);
        a.setMarca(campoMarca.getText().trim());
        a.setModelo(campoModelo.getText().trim());
        a.setNumeroSerie(campoSerie.getText().trim());
        a.setIdCliente(dono.getId());
        try {
            if (idSelecionado > 0) dao.atualizar(a);
            else dao.inserir(a);
            carregar();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Não gravou.\n" + ex.getMessage());
        }
    }

    private void excluir() {
        if (idSelecionado <= 0) {
            JOptionPane.showMessageDialog(this, "Selecione um aparelho na tabela.");
            return;
        }
        int ok = JOptionPane.showConfirmDialog(this, "Excluir o aparelho selecionado?", "Confirmar",
                JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) return;
        try {
            dao.excluir(idSelecionado);
            carregar();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Não excluiu.\n" + ex.getMessage()
                    + "\n(Aparelho com OS não pode ser excluído.)");
        }
    }

    private void limpar() {
        idSelecionado = 0;
        tabela.clearSelection();
        campoTipo.setText("");
        campoMarca.setText("");
        campoModelo.setText("");
        campoSerie.setText("");
        if (comboCliente.getItemCount() > 0) comboCliente.setSelectedIndex(0);
    }

    private String texto(Object o) { return o == null ? "" : o.toString(); }
}
