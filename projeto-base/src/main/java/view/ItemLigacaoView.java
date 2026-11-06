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

import dao.ItemBDAO;
import dao.ItemDAO;
import dao.ItemLigacaoDAO;
import model.Item;
import model.ItemB;
import model.ItemLigacao;

/**
 * ItemLigacaoView — tela da ENTIDADE DE LIGAÇÃO. Molde da Ordem de Serviço.
 *
 * O QUE ESTA TELA MOSTRA DE NOVO (em relação à ItemFilhoView):
 *   - DOIS combos de "pai" (um para cada FK), cada um alimentado pelo seu DAO;
 *   - um campo de DATA + HORA, convertido de texto para LocalDateTime;
 *   - vários campos próprios (status, valor, descrição).
 *
 * Fora isso, o fluxo é o mesmo molde de sempre:
 *   carregar -> selecionar linha -> Salvar (insere/atualiza) -> Excluir -> Novo.
 *
 * [MOLDE] Para a sua entidade central (ex.: Ordem de Serviço):
 *   1. troque ItemLigacao/ItemLigacaoDAO pela sua entidade/DAO;
 *   2. troque ItemDAO e ItemBDAO pelos DAOs dos pais reais (Cliente, Técnico...);
 *   3. para um 3º pai, copie um bloco de combo (comboX + carregarComboX);
 *   4. ajuste os campos próprios do formulário e as colunas da tabela.
 */
public class ItemLigacaoView extends JFrame {

    private final ItemLigacaoDAO dao = new ItemLigacaoDAO(); // DAO da ligação
    private final ItemDAO itemDao = new ItemDAO();            // DAO do 1º pai
    private final ItemBDAO itemBDao = new ItemBDAO();         // DAO do 2º pai

    // Formato de data/hora usado na tela. Ex.: 06/11/2026 14:30
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final JTextField campoBusca = new JTextField(16);
    private final JComboBox<Item> comboItem = new JComboBox<>();   // 1º pai
    private final JComboBox<ItemB> comboItemB = new JComboBox<>(); // 2º pai
    private final JTextField campoData = new JTextField();         // "dd/MM/yyyy HH:mm"
    private final JTextField campoStatus = new JTextField();
    private final JTextField campoValor = new JTextField();
    private final JTextField campoDescricao = new JTextField();

    private int idSelecionado = 0;

    private final DefaultTableModel modelo = new DefaultTableModel(
            new String[] { "ID", "Item (1º pai)", "Item B (2º pai)", "Data/Hora", "Status", "Valor", "Descrição" }, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable tabela = new JTable(modelo);

    public ItemLigacaoView() {
        setTitle("Ligações (molde da Ordem de Serviço)");
        setSize(900, 560);
        setLocationRelativeTo(null);

        // ----- NORTE: busca -----
        JPanel busca = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.addActionListener(e -> carregar());
        busca.add(new JLabel("Buscar (nome dos pais ou status)"));
        busca.add(campoBusca);
        busca.add(btnBuscar);

        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.getSelectionModel().addListSelectionListener(e -> preencherSelecionado());

        // Faz cada combo exibir o NOME da entidade (em vez do endereço do objeto).
        comboItem.setRenderer(new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(JList<?> l, Object v, int i, boolean s, boolean f) {
                super.getListCellRendererComponent(l, v, i, s, f);
                if (v instanceof Item it) setText(it.getNome());
                return this;
            }
        });
        comboItemB.setRenderer(new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(JList<?> l, Object v, int i, boolean s, boolean f) {
                super.getListCellRendererComponent(l, v, i, s, f);
                if (v instanceof ItemB b) setText(b.getNome());
                return this;
            }
        });

        // ----- SUL: formulário (grade de 6 linhas x 2 colunas) -----
        JPanel form = new JPanel(new GridLayout(6, 2, 8, 8));
        form.setBorder(BorderFactory.createTitledBorder("Dados da ligação"));
        form.add(new JLabel("Item (1º pai)"));
        form.add(comboItem);
        form.add(new JLabel("Item B (2º pai)"));
        form.add(comboItemB);
        form.add(new JLabel("Data/Hora (dd/MM/aaaa HH:mm)"));
        form.add(campoData);
        form.add(new JLabel("Status"));
        form.add(campoStatus);
        form.add(new JLabel("Valor"));
        form.add(campoValor);
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

        JLabel faixa = new JLabel("  Uma ligação PRECISA dos dois pais. Os combos listam os cadastros existentes.");
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
        carregarCombos(); // enche os dois seletores de pais ANTES de carregar a tabela
        carregar();
    }

    /** Preenche os dois combos com os pais vindos dos respectivos DAOs. */
    private void carregarCombos() {
        DefaultComboBoxModel<Item> ma = new DefaultComboBoxModel<>();
        for (Item i : itemDao.listar("")) ma.addElement(i);
        comboItem.setModel(ma);

        DefaultComboBoxModel<ItemB> mb = new DefaultComboBoxModel<>();
        for (ItemB b : itemBDao.listar("")) mb.addElement(b);
        comboItemB.setModel(mb);
    }

    /** READ — pede a lista ao DAO e joga cada registro em uma linha da tabela. */
    private void carregar() {
        modelo.setRowCount(0);
        List<ItemLigacao> lista = dao.listar(campoBusca.getText());
        for (ItemLigacao l : lista) {
            String data = l.getDataRegistro() == null ? "" : l.getDataRegistro().format(FMT);
            modelo.addRow(new Object[] {
                    l.getId(), l.getNomeItem(), l.getNomeItemB(), data,
                    l.getStatus(), l.getValor(), l.getDescricao() });
        }
        limpar();
    }

    /** Ao clicar numa linha, copia os valores para o formulário e seleciona os dois pais. */
    private void preencherSelecionado() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) return;
        idSelecionado = (int) modelo.getValueAt(linha, 0);
        selecionarNoCombo(comboItem, texto(modelo.getValueAt(linha, 1)));   // 1º pai pelo nome
        selecionarNoComboB(comboItemB, texto(modelo.getValueAt(linha, 2))); // 2º pai pelo nome
        campoData.setText(texto(modelo.getValueAt(linha, 3)));
        campoStatus.setText(texto(modelo.getValueAt(linha, 4)));
        campoValor.setText(texto(modelo.getValueAt(linha, 5)));
        campoDescricao.setText(texto(modelo.getValueAt(linha, 6)));
    }

    /** CREATE ou UPDATE — valida tudo e grava. Decide pelo idSelecionado. */
    private void salvar() {
        Item pai1 = (Item) comboItem.getSelectedItem();
        ItemB pai2 = (ItemB) comboItemB.getSelectedItem();
        if (pai1 == null || pai2 == null) {
            JOptionPane.showMessageDialog(this, "Cadastre os dois pais (Item e Item B) antes de criar uma ligação.");
            return;
        }

        // Converte o texto da data em LocalDateTime. Se vier vazio, usa "agora".
        LocalDateTime data;
        String textoData = campoData.getText().trim();
        try {
            data = textoData.isEmpty() ? LocalDateTime.now() : LocalDateTime.parse(textoData, FMT);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Data inválida. Use o formato dd/MM/aaaa HH:mm (ex.: 06/11/2026 14:30).");
            return;
        }

        // Converte o valor (aceita vírgula ou ponto). Vazio = 0.
        double valor = 0;
        try {
            String v = campoValor.getText().trim().replace(",", ".");
            if (!v.isEmpty()) valor = Double.parseDouble(v);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Valor deve ser um número (ex.: 150.00).");
            return;
        }

        ItemLigacao l = new ItemLigacao();
        l.setId(idSelecionado);
        l.setIdItem(pai1.getId());   // FK 1
        l.setIdItemB(pai2.getId());  // FK 2
        l.setDataRegistro(data);
        l.setStatus(campoStatus.getText().trim());
        l.setValor(valor);
        l.setDescricao(campoDescricao.getText().trim());
        try {
            if (idSelecionado > 0) dao.atualizar(l);
            else dao.inserir(l);
            carregar();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Não gravou.\n" + ex.getMessage());
        }
    }

    /** DELETE — apaga o registro selecionado, pedindo confirmação. */
    private void excluir() {
        if (idSelecionado <= 0) {
            JOptionPane.showMessageDialog(this, "Selecione uma ligação na tabela.");
            return;
        }
        int ok = JOptionPane.showConfirmDialog(this, "Excluir a ligação selecionada?", "Confirmar",
                JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) return;
        try {
            dao.excluir(idSelecionado);
            carregar();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Não excluiu.\n" + ex.getMessage());
        }
    }

    /** Zera a seleção e os campos: estado "pronto para uma nova ligação". */
    private void limpar() {
        idSelecionado = 0;
        tabela.clearSelection();
        if (comboItem.getItemCount() > 0) comboItem.setSelectedIndex(0);
        if (comboItemB.getItemCount() > 0) comboItemB.setSelectedIndex(0);
        campoData.setText("");
        campoStatus.setText("");
        campoValor.setText("");
        campoDescricao.setText("");
    }

    /** Seleciona no combo de Item a opção cujo nome bate com o texto dado. */
    private void selecionarNoCombo(JComboBox<Item> combo, String nome) {
        for (int k = 0; k < combo.getItemCount(); k++) {
            if (combo.getItemAt(k).getNome().equals(nome)) { combo.setSelectedIndex(k); return; }
        }
    }

    /** Seleciona no combo de ItemB a opção cujo nome bate com o texto dado. */
    private void selecionarNoComboB(JComboBox<ItemB> combo, String nome) {
        for (int k = 0; k < combo.getItemCount(); k++) {
            if (combo.getItemAt(k).getNome().equals(nome)) { combo.setSelectedIndex(k); return; }
        }
    }

    private String texto(Object o) {
        return o == null ? "" : o.toString();
    }
}
