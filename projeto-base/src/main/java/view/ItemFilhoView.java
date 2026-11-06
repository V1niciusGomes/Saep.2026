package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import dao.ItemDAO;
import dao.ItemFilhoDAO;
import model.Item;
import model.ItemFilho;

/**
 * ItemFilhoView — tela de cadastro de uma entidade que PERTENCE A outra.
 * Mesmo molde da ItemView, com UM recurso a mais: um seletor (JComboBox) para
 * escolher o "pai" (o item ao qual o filho pertence).
 *
 * O SELETOR DE PAI (JComboBox<Item>):
 *   - é preenchido com os itens vindos do ItemDAO (combo guarda objetos Item);
 *   - ao salvar, pegamos o Item escolhido e usamos o id dele como FK;
 *   - ao editar uma linha, selecionamos no combo o item correspondente.
 *
 * [MOLDE] Para uma tela "filha" nova (ex.: ItemDoPedido escolhendo um Pedido):
 *   1. troque ItemFilho/ItemFilhoDAO pela sua entidade/DAO;
 *   2. troque ItemDAO pelo DAO do "pai" que alimenta o combo;
 *   3. ajuste os campos e colunas como na ItemView.
 */
public class ItemFilhoView extends JFrame {

    private final ItemFilhoDAO dao = new ItemFilhoDAO(); // DAO do filho
    private final ItemDAO itemDao = new ItemDAO();        // DAO do pai (alimenta o combo)

    // Campos da tela.
    private final JTextField campoBusca = new JTextField(16);
    private final JTextField campoNome = new JTextField();
    private final JTextField campoValor = new JTextField();
    // Combo que lista os possíveis "pais". Guarda objetos Item (não só texto).
    private final JComboBox<Item> comboItem = new JComboBox<>();

    private int idSelecionado = 0;

    private final DefaultTableModel modelo = new DefaultTableModel(
            new String[] { "ID", "Nome", "Valor", "Item (pai)" }, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable tabela = new JTable(modelo);

    public ItemFilhoView() {
        setTitle("Itens Filhos");
        setSize(760, 520);
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

        // Faz o combo mostrar o NOME do item (em vez do endereço do objeto).
        comboItem.setRenderer(new javax.swing.DefaultListCellRenderer() {
            @Override public java.awt.Component getListCellRendererComponent(
                    javax.swing.JList<?> lista, Object valor, int indice,
                    boolean selecionado, boolean comFoco) {
                super.getListCellRendererComponent(lista, valor, indice, selecionado, comFoco);
                if (valor instanceof Item it) setText(it.getNome());
                return this;
            }
        });

        // ----- SUL: formulário -----
        JPanel form = new JPanel(new GridLayout(3, 2, 8, 8));
        form.setBorder(BorderFactory.createTitledBorder("Dados do filho"));
        form.add(new JLabel("Nome"));
        form.add(campoNome);
        form.add(new JLabel("Valor"));
        form.add(campoValor);
        form.add(new JLabel("Item (pai)"));
        form.add(comboItem);

        // ----- SUL: botões -----
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

        JLabel faixa = new JLabel("  Todo filho PRECISA de um item (pai). O combo lista os itens cadastrados.");
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
        carregarCombo(); // enche o seletor de pais ANTES de carregar a tabela
        carregar();
    }

    /** Preenche o combo com todos os itens (pais) vindos do banco. */
    private void carregarCombo() {
        DefaultComboBoxModel<Item> m = new DefaultComboBoxModel<>();
        for (Item i : itemDao.listar("")) { // filtro vazio = todos
            m.addElement(i);
        }
        comboItem.setModel(m);
    }

    /** READ — pede a lista ao DAO e joga cada registro em uma linha da tabela. */
    private void carregar() {
        modelo.setRowCount(0);
        List<ItemFilho> lista = dao.listar(campoBusca.getText());
        for (ItemFilho f : lista) {
            modelo.addRow(new Object[] { f.getId(), f.getNome(), f.getValor(), f.getNomeItem() });
        }
        limpar();
    }

    /** Ao clicar numa linha, copia os valores para o formulário e seleciona o pai no combo. */
    private void preencherSelecionado() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) return;
        idSelecionado = (int) modelo.getValueAt(linha, 0);
        campoNome.setText(texto(modelo.getValueAt(linha, 1)));
        campoValor.setText(texto(modelo.getValueAt(linha, 2)));
        // Seleciona no combo o item cujo nome bate com o da coluna "Item (pai)".
        String nomePai = texto(modelo.getValueAt(linha, 3));
        for (int k = 0; k < comboItem.getItemCount(); k++) {
            if (comboItem.getItemAt(k).getNome().equals(nomePai)) {
                comboItem.setSelectedIndex(k);
                break;
            }
        }
    }

    /** CREATE ou UPDATE — valida e grava. Decide pelo idSelecionado. */
    private void salvar() {
        String nome = campoNome.getText().trim();
        if (nome.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nome é obrigatório.");
            return;
        }
        Item pai = (Item) comboItem.getSelectedItem();
        if (pai == null) {
            JOptionPane.showMessageDialog(this, "Cadastre um item (pai) antes de criar um filho.");
            return;
        }
        // Converte o texto do valor em número; se vier vazio/errado, usa 0.
        double valor = 0;
        try {
            String v = campoValor.getText().trim().replace(",", ".");
            if (!v.isEmpty()) valor = Double.parseDouble(v);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Valor deve ser um número (ex.: 10.50).");
            return;
        }

        ItemFilho f = new ItemFilho();
        f.setId(idSelecionado);
        f.setNome(nome);
        f.setValor(valor);
        f.setIdItem(pai.getId()); // a FK: id do pai escolhido no combo
        try {
            if (idSelecionado > 0) {
                dao.atualizar(f);
            } else {
                dao.inserir(f);
            }
            carregar();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Não gravou.\n" + ex.getMessage());
        }
    }

    /** DELETE — apaga o registro selecionado, pedindo confirmação. */
    private void excluir() {
        if (idSelecionado <= 0) {
            JOptionPane.showMessageDialog(this, "Selecione um filho na tabela.");
            return;
        }
        int ok = JOptionPane.showConfirmDialog(this, "Excluir o filho selecionado?", "Confirmar",
                JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) return;
        try {
            dao.excluir(idSelecionado);
            carregar();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Não excluiu.\n" + ex.getMessage());
        }
    }

    /** Zera a seleção e os campos: estado "pronto para um novo cadastro". */
    private void limpar() {
        idSelecionado = 0;
        tabela.clearSelection();
        campoNome.setText("");
        campoValor.setText("");
        if (comboItem.getItemCount() > 0) comboItem.setSelectedIndex(0);
    }

    /** Converte valor da tabela em texto seguro (evita NullPointer se vier null). */
    private String texto(Object o) {
        return o == null ? "" : o.toString();
    }
}
