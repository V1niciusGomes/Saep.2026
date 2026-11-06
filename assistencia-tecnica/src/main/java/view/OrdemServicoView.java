package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
import dao.OrdemServicoDAO;
import dao.TecnicoDAO;
import model.Aparelho;
import model.Cliente;
import model.OrdemServico;
import model.Tecnico;

/**
 * OrdemServicoView — a tela CENTRAL (molde ItemLigacao com TRÊS pais).
 * Tem três combos (cliente, técnico, aparelho), campo de data/hora e os
 * campos próprios (status, valor da mão de obra, defeito relatado).
 */
public class OrdemServicoView extends JFrame {

    private final OrdemServicoDAO dao = new OrdemServicoDAO();
    private final ClienteDAO clienteDao = new ClienteDAO();
    private final TecnicoDAO tecnicoDao = new TecnicoDAO();
    private final AparelhoDAO aparelhoDao = new AparelhoDAO();

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final JTextField campoBusca = new JTextField(16);
    private final JComboBox<Cliente> comboCliente = new JComboBox<>();
    private final JComboBox<Tecnico> comboTecnico = new JComboBox<>();
    private final JComboBox<Aparelho> comboAparelho = new JComboBox<>();
    private final JTextField campoData = new JTextField();
    private final JTextField campoStatus = new JTextField();
    private final JTextField campoValor = new JTextField();
    private final JTextField campoDefeito = new JTextField();

    private int idSelecionado = 0;

    private final DefaultTableModel modelo = new DefaultTableModel(
            new String[] { "ID", "Cliente", "Técnico", "Aparelho", "Abertura", "Status", "Mão de obra", "Defeito" }, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable tabela = new JTable(modelo);

    public OrdemServicoView() {
        setTitle("Ordens de Serviço");
        setSize(1000, 600);
        setLocationRelativeTo(null);

        JPanel busca = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.addActionListener(e -> carregar());
        busca.add(new JLabel("Cliente/técnico/status"));
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
        comboTecnico.setRenderer(new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(JList<?> l, Object v, int i, boolean s, boolean f) {
                super.getListCellRendererComponent(l, v, i, s, f);
                if (v instanceof Tecnico t) setText(t.getNome());
                return this;
            }
        });
        comboAparelho.setRenderer(new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(JList<?> l, Object v, int i, boolean s, boolean f) {
                super.getListCellRendererComponent(l, v, i, s, f);
                if (v instanceof Aparelho a) {
                    setText(a.getTipo() + " " + nz(a.getMarca()) + " " + nz(a.getModelo())
                            + " (" + a.getNomeCliente() + ")");
                }
                return this;
            }
        });

        JPanel form = new JPanel(new GridLayout(7, 2, 8, 8));
        form.setBorder(BorderFactory.createTitledBorder("Dados da ordem de serviço"));
        form.add(new JLabel("Cliente"));   form.add(comboCliente);
        form.add(new JLabel("Técnico"));   form.add(comboTecnico);
        form.add(new JLabel("Aparelho"));  form.add(comboAparelho);
        form.add(new JLabel("Abertura (dd/MM/aaaa HH:mm)")); form.add(campoData);
        form.add(new JLabel("Status"));    form.add(campoStatus);
        form.add(new JLabel("Mão de obra (R$)")); form.add(campoValor);
        form.add(new JLabel("Defeito relatado")); form.add(campoDefeito);

        JButton btnNovo = new JButton("Novo");
        JButton btnSalvar = new JButton("Salvar");
        JButton btnExcluir = new JButton("Excluir");
        btnNovo.addActionListener(e -> limpar());
        btnSalvar.addActionListener(e -> salvar());
        btnExcluir.addActionListener(e -> excluir());
        JPanel acoes = new JPanel(new GridLayout(3, 1, 8, 8));
        acoes.add(btnNovo); acoes.add(btnSalvar); acoes.add(btnExcluir);

        JLabel faixa = new JLabel("  Uma OS precisa de cliente, técnico e aparelho. Cadastre-os antes nos menus.");
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
        DefaultComboBoxModel<Cliente> mc = new DefaultComboBoxModel<>();
        for (Cliente c : clienteDao.listar("")) mc.addElement(c);
        comboCliente.setModel(mc);

        DefaultComboBoxModel<Tecnico> mt = new DefaultComboBoxModel<>();
        for (Tecnico t : tecnicoDao.listar("")) mt.addElement(t);
        comboTecnico.setModel(mt);

        DefaultComboBoxModel<Aparelho> ma = new DefaultComboBoxModel<>();
        for (Aparelho a : aparelhoDao.listar("")) ma.addElement(a);
        comboAparelho.setModel(ma);
    }

    private void carregar() {
        modelo.setRowCount(0);
        List<OrdemServico> lista = dao.listar(campoBusca.getText());
        for (OrdemServico o : lista) {
            String data = o.getDataAbertura() == null ? "" : o.getDataAbertura().format(FMT);
            modelo.addRow(new Object[] {
                    o.getId(), o.getNomeCliente(), o.getNomeTecnico(), o.getDescricaoAparelho(),
                    data, o.getStatus(), o.getValorMaoObra(), o.getDefeitoRelatado() });
        }
        limpar();
    }

    private void preencherSelecionado() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) return;
        idSelecionado = (int) modelo.getValueAt(linha, 0);
        selecionarCliente(texto(modelo.getValueAt(linha, 1)));
        selecionarTecnico(texto(modelo.getValueAt(linha, 2)));
        // O aparelho é selecionado pela descrição montada no renderer.
        String descAparelho = texto(modelo.getValueAt(linha, 3));
        for (int k = 0; k < comboAparelho.getItemCount(); k++) {
            Aparelho a = comboAparelho.getItemAt(k);
            String desc = a.getTipo() + " " + nz(a.getMarca()) + " " + nz(a.getModelo());
            if (descAparelho.startsWith(desc.trim())) { comboAparelho.setSelectedIndex(k); break; }
        }
        campoData.setText(texto(modelo.getValueAt(linha, 4)));
        campoStatus.setText(texto(modelo.getValueAt(linha, 5)));
        campoValor.setText(texto(modelo.getValueAt(linha, 6)));
        campoDefeito.setText(texto(modelo.getValueAt(linha, 7)));
    }

    private void salvar() {
        Cliente cliente = (Cliente) comboCliente.getSelectedItem();
        Tecnico tecnico = (Tecnico) comboTecnico.getSelectedItem();
        Aparelho aparelho = (Aparelho) comboAparelho.getSelectedItem();
        if (cliente == null || tecnico == null || aparelho == null) {
            JOptionPane.showMessageDialog(this, "Selecione cliente, técnico e aparelho. Cadastre-os antes se faltar algum.");
            return;
        }
        LocalDateTime data;
        String textoData = campoData.getText().trim();
        try {
            data = textoData.isEmpty() ? LocalDateTime.now() : LocalDateTime.parse(textoData, FMT);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Data inválida. Use dd/MM/aaaa HH:mm (ex.: 06/11/2026 14:30).");
            return;
        }
        double valor = 0;
        try {
            String v = campoValor.getText().trim().replace(",", ".");
            if (!v.isEmpty()) valor = Double.parseDouble(v);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Mão de obra deve ser um número (ex.: 80.00).");
            return;
        }

        OrdemServico o = new OrdemServico();
        o.setId(idSelecionado);
        o.setIdCliente(cliente.getId());
        o.setIdTecnico(tecnico.getId());
        o.setIdAparelho(aparelho.getId());
        o.setDataAbertura(data);
        o.setStatus(campoStatus.getText().trim());
        o.setValorMaoObra(valor);
        o.setDefeitoRelatado(campoDefeito.getText().trim());
        try {
            if (idSelecionado > 0) dao.atualizar(o);
            else dao.inserir(o);
            carregar();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Não gravou.\n" + ex.getMessage());
        }
    }

    private void excluir() {
        if (idSelecionado <= 0) {
            JOptionPane.showMessageDialog(this, "Selecione uma OS na tabela.");
            return;
        }
        int ok = JOptionPane.showConfirmDialog(this, "Excluir a OS selecionada?", "Confirmar",
                JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) return;
        try {
            dao.excluir(idSelecionado);
            carregar();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Não excluiu.\n" + ex.getMessage()
                    + "\n(OS com peças lançadas não pode ser excluída.)");
        }
    }

    private void limpar() {
        idSelecionado = 0;
        tabela.clearSelection();
        if (comboCliente.getItemCount() > 0) comboCliente.setSelectedIndex(0);
        if (comboTecnico.getItemCount() > 0) comboTecnico.setSelectedIndex(0);
        if (comboAparelho.getItemCount() > 0) comboAparelho.setSelectedIndex(0);
        campoData.setText("");
        campoStatus.setText("");
        campoValor.setText("");
        campoDefeito.setText("");
    }

    private void selecionarCliente(String nome) {
        for (int k = 0; k < comboCliente.getItemCount(); k++) {
            if (comboCliente.getItemAt(k).getNome().equals(nome)) { comboCliente.setSelectedIndex(k); return; }
        }
    }

    private void selecionarTecnico(String nome) {
        for (int k = 0; k < comboTecnico.getItemCount(); k++) {
            if (comboTecnico.getItemAt(k).getNome().equals(nome)) { comboTecnico.setSelectedIndex(k); return; }
        }
    }

    private String nz(String s) { return s == null ? "" : s; }
    private String texto(Object o) { return o == null ? "" : o.toString(); }
}
