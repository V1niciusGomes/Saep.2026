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

import dao.ItemDAO;
import model.Item;

/**
 * ItemView — EXEMPLO-MESTRE de uma tela de cadastro (CRUD).
 *
 * PAPEL NA ARQUITETURA (camada view):
 *   A tela cuida só do visual e da interação. Para ler/gravar, ela chama o
 *   DAO. Para transportar dados, usa o model. Nunca escreve SQL.
 *
 * LAYOUT PADRÃO (igual em todas as telas de cadastro):
 *   NORTE  = painel de busca (campo + botão Buscar)
 *   CENTRO = tabela (JTable) com os registros
 *   SUL    = formulário + botões (Novo/Salvar/Excluir) + faixa de aviso
 *
 * FLUXO PADRÃO:
 *   - carregar()  : lê do DAO e preenche a tabela;
 *   - clicar numa linha -> preencherSelecionado() joga os dados no formulário;
 *   - Salvar      : se há linha selecionada, atualiza; senão, insere;
 *   - Excluir     : apaga a linha selecionada (com confirmação);
 *   - Novo/limpar : zera o formulário para um cadastro em branco.
 *
 * [MOLDE] Para uma tela nova, copie esta classe e:
 *   1. troque Item/ItemDAO pela sua entidade/DAO;
 *   2. troque os JTextField pelos campos do seu cadastro;
 *   3. ajuste os títulos das colunas e o que vai em cada addRow/get/set.
 */
public class ItemView extends JFrame {

    // DAO: a ponte com o banco.
    private final ItemDAO dao = new ItemDAO();

    // Campos da tela. [MOLDE] troque conforme os campos da sua entidade.
    private final JTextField campoBusca = new JTextField(16);
    private final JTextField campoNome = new JTextField();
    private final JTextField campoDescricao = new JTextField();
    private final JTextField campoCodigo = new JTextField();

    // Guarda o id da linha selecionada. 0 = nenhum (modo "inserir novo").
    private int idSelecionado = 0;

    // Modelo da tabela: define as COLUNAS e segura as LINHAS.
    // isCellEditable=false deixa a tabela só para leitura (edição é pelo formulário).
    private final DefaultTableModel modelo = new DefaultTableModel(
            new String[] { "ID", "Nome", "Descrição", "Código" }, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable tabela = new JTable(modelo);

    public ItemView() {
        setTitle("Itens");
        setSize(760, 520);
        setLocationRelativeTo(null);

        // ----- NORTE: busca -----
        JPanel busca = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.addActionListener(e -> carregar()); // Buscar = recarrega aplicando o filtro
        busca.add(new JLabel("Nome"));
        busca.add(campoBusca);
        busca.add(btnBuscar);

        // Seleção de 1 linha por vez; ao selecionar, joga os dados no formulário.
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.getSelectionModel().addListSelectionListener(e -> preencherSelecionado());

        // ----- SUL: formulário (rótulo + campo em grade 3x2) -----
        JPanel form = new JPanel(new GridLayout(3, 2, 8, 8));
        form.setBorder(BorderFactory.createTitledBorder("Dados do item"));
        form.add(new JLabel("Nome"));
        form.add(campoNome);
        form.add(new JLabel("Descrição"));
        form.add(campoDescricao);
        form.add(new JLabel("Código"));
        form.add(campoCodigo);

        // ----- SUL: botões de ação -----
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

        // Faixa de aviso (dica fixa para o usuário).
        JLabel faixa = new JLabel("  ID é gerado pelo MySQL (AUTO_INCREMENT). Código não pode repetir (UNIQUE).");
        faixa.setOpaque(true);
        faixa.setBackground(new Color(232, 241, 248));
        faixa.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        // Agrupa formulário + ações + faixa no painel do SUL.
        JPanel sul = new JPanel(new BorderLayout(8, 8));
        sul.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        sul.add(form, BorderLayout.CENTER);
        sul.add(acoes, BorderLayout.EAST);
        sul.add(faixa, BorderLayout.SOUTH);

        // Monta a janela nas três regiões.
        add(busca, BorderLayout.NORTH);
        add(new JScrollPane(tabela), BorderLayout.CENTER); // scroll caso tenha muitas linhas
        add(sul, BorderLayout.SOUTH);

        getRootPane().setDefaultButton(btnBuscar); // Enter aciona a busca
        carregar(); // já abre a tela com os dados carregados
    }

    /** READ — pede a lista ao DAO e joga cada registro em uma linha da tabela. */
    private void carregar() {
        modelo.setRowCount(0); // limpa a tabela antes de recarregar
        List<Item> lista = dao.listar(campoBusca.getText());
        for (Item i : lista) {
            // A ORDEM aqui tem que casar com a ordem das colunas lá em cima.
            modelo.addRow(new Object[] { i.getId(), i.getNome(), i.getDescricao(), i.getCodigo() });
        }
        limpar(); // começa sem seleção, formulário em branco
    }

    /** Ao clicar numa linha, copia os valores dela para os campos do formulário. */
    private void preencherSelecionado() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) return; // nada selecionado
        // Coluna 0 é o ID; guardamos para saber que é um UPDATE no Salvar.
        idSelecionado = (int) modelo.getValueAt(linha, 0);
        campoNome.setText(texto(modelo.getValueAt(linha, 1)));
        campoDescricao.setText(texto(modelo.getValueAt(linha, 2)));
        campoCodigo.setText(texto(modelo.getValueAt(linha, 3)));
    }

    /** CREATE ou UPDATE — valida e grava. Decide pelo idSelecionado. */
    private void salvar() {
        String nome = campoNome.getText().trim();
        if (nome.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nome é obrigatório.");
            return;
        }
        // Monta o model a partir dos campos da tela.
        Item i = new Item();
        i.setId(idSelecionado);
        i.setNome(nome);
        i.setDescricao(campoDescricao.getText().trim());
        i.setCodigo(campoCodigo.getText().trim());
        try {
            if (idSelecionado > 0) {
                dao.atualizar(i); // havia linha selecionada -> edição
            } else {
                dao.inserir(i);   // nenhuma seleção -> novo registro
            }
            carregar(); // recarrega a tabela para refletir a mudança
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Não gravou.\n" + ex.getMessage());
        }
    }

    /** DELETE — apaga o registro selecionado, pedindo confirmação. */
    private void excluir() {
        if (idSelecionado <= 0) {
            JOptionPane.showMessageDialog(this, "Selecione um item na tabela.");
            return;
        }
        int ok = JOptionPane.showConfirmDialog(this, "Excluir o item selecionado?", "Confirmar",
                JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) return; // usuário desistiu
        try {
            dao.excluir(idSelecionado);
            carregar();
        } catch (Exception ex) {
            // Ex.: item com filhos vinculados (a FK impede apagar).
            JOptionPane.showMessageDialog(this, "Não excluiu.\n" + ex.getMessage()
                    + "\n(Item com filhos cadastrados não pode ser excluído.)");
        }
    }

    /** Zera a seleção e os campos: estado "pronto para um novo cadastro". */
    private void limpar() {
        idSelecionado = 0;
        tabela.clearSelection();
        campoNome.setText("");
        campoDescricao.setText("");
        campoCodigo.setText("");
    }

    /** Converte valor da tabela em texto seguro (evita NullPointer se vier null). */
    private String texto(Object o) {
        return o == null ? "" : o.toString();
    }
}
