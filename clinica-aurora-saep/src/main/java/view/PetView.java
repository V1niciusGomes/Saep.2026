package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.time.LocalDate;
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

import dao.PetDAO;
import dao.TutorDAO;
import model.Pet;
import model.Tutor;

/**
 * PetView — tela de CRUD de uma entidade que PERTENCE A outra (pet tem tutor).
 * Segue o molde do TutorView, com DUAS novidades:
 *
 *   1. JComboBox<Tutor> comboTutor
 *      Lista de escolha com os tutores. Importante: o combo guarda o OBJETO
 *      Tutor inteiro (não só o texto), então na hora de salvar pegamos o
 *      tutor.getId() para gravar na FK. Um "renderer" (lá embaixo) faz o combo
 *      exibir só o nome do tutor.
 *
 *   2. Campo de data (texto "aaaa-mm-dd") convertido para LocalDate com
 *      parseData(). Se o usuário digitar errado, avisamos sem quebrar.
 *
 * [MOLDE] Para uma "entidade filha" (Produto->Categoria): troque o combo de
 *         Tutor pelo combo da entidade-pai e carregue-o com o DAO do pai.
 */
public class PetView extends JFrame {

    private final PetDAO dao = new PetDAO();
    private final TutorDAO tutorDao = new TutorDAO(); // usado só para encher o combo de tutores

    private final JTextField campoBusca = new JTextField(16);
    private final JTextField campoNome = new JTextField();
    private final JTextField campoEspecie = new JTextField();
    private final JTextField campoRaca = new JTextField();
    private final JTextField campoNascimento = new JTextField();
    private final JComboBox<Tutor> comboTutor = new JComboBox<>(); // seletor do "dono"
    private int idSelecionado = 0;

    private final DefaultTableModel modelo = new DefaultTableModel(
            new String[] { "ID", "Nome", "Espécie", "Raça", "Nascimento", "Tutor" }, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable tabela = new JTable(modelo);

    public PetView() {
        setTitle("Pets");
        setSize(820, 540);
        setLocationRelativeTo(null);

        // Faz o combo mostrar o NOME do tutor (e não o endereço do objeto).
        comboTutor.setRenderer(new TutorRenderer());

        // NORTE: busca
        JPanel busca = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.addActionListener(e -> carregar());
        busca.add(new JLabel("Pet ou tutor"));
        busca.add(campoBusca);
        busca.add(btnBuscar);

        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.getSelectionModel().addListSelectionListener(e -> preencherSelecionado());

        // SUL: formulário (note o combo de tutor na última linha)
        JPanel form = new JPanel(new GridLayout(5, 2, 8, 8));
        form.setBorder(BorderFactory.createTitledBorder("Dados do pet"));
        form.add(new JLabel("Nome"));
        form.add(campoNome);
        form.add(new JLabel("Espécie"));
        form.add(campoEspecie);
        form.add(new JLabel("Raça"));
        form.add(campoRaca);
        form.add(new JLabel("Nascimento (aaaa-mm-dd)"));
        form.add(campoNascimento);
        form.add(new JLabel("Tutor"));
        form.add(comboTutor);

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

        JLabel faixa = new JLabel("  Pet sem tutor não grava (FK pet \u2192 tutor). O tutor vem da lista.");
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
        carregarTutores(); // enche o combo ANTES de carregar a tabela
        carregar();
    }

    /** Preenche o combo com todos os tutores do banco. */
    private void carregarTutores() {
        DefaultComboBoxModel<Tutor> cm = new DefaultComboBoxModel<>();
        for (Tutor t : tutorDao.listar("")) { // "" = traz todos
            cm.addElement(t); // guarda o Tutor inteiro (com id) em cada item
        }
        comboTutor.setModel(cm);
    }

    /** READ — carrega os pets na tabela. */
    private void carregar() {
        modelo.setRowCount(0);
        List<Pet> lista = dao.listar(campoBusca.getText());
        for (Pet p : lista) {
            modelo.addRow(new Object[] { p.getId(), p.getNome(), p.getEspecie(), p.getRaca(),
                    p.getDataNascimento(), p.getNomeTutor() });
        }
        limpar();
    }

    /** Copia a linha clicada para o formulário (e seleciona o tutor no combo). */
    private void preencherSelecionado() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) return;
        idSelecionado = (int) modelo.getValueAt(linha, 0);
        campoNome.setText(texto(modelo.getValueAt(linha, 1)));
        campoEspecie.setText(texto(modelo.getValueAt(linha, 2)));
        campoRaca.setText(texto(modelo.getValueAt(linha, 3)));
        campoNascimento.setText(texto(modelo.getValueAt(linha, 4)));
        // A tabela mostra o NOME do tutor; aqui procuramos o item do combo com esse nome.
        selecionarTutorPorNome(texto(modelo.getValueAt(linha, 5)));
    }

    /** Percorre o combo e seleciona o tutor cujo nome bate com o da tabela. */
    private void selecionarTutorPorNome(String nome) {
        for (int i = 0; i < comboTutor.getItemCount(); i++) {
            if (comboTutor.getItemAt(i).getNome().equals(nome)) {
                comboTutor.setSelectedIndex(i);
                return;
            }
        }
    }

    /** CREATE/UPDATE — valida, resolve o tutor escolhido e grava. */
    private void salvar() {
        String nome = campoNome.getText().trim();
        String especie = campoEspecie.getText().trim();
        Tutor tutor = (Tutor) comboTutor.getSelectedItem(); // o dono escolhido
        if (nome.isEmpty() || especie.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nome e espécie são obrigatórios.");
            return;
        }
        if (tutor == null) {
            JOptionPane.showMessageDialog(this, "Escolha um tutor. Cadastre um tutor antes se a lista estiver vazia.");
            return;
        }
        Pet p = new Pet();
        p.setId(idSelecionado);
        p.setNome(nome);
        p.setEspecie(especie);
        p.setRaca(campoRaca.getText().trim());
        p.setIdTutor(tutor.getId()); // AQUI a FK é preenchida com o id do tutor escolhido

        // Converte a data digitada. Se estiver preenchida mas inválida, avisa.
        LocalDate data = parseData(campoNascimento.getText().trim());
        if (data == null && !campoNascimento.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Data inválida. Use o formato aaaa-mm-dd.");
            return;
        }
        p.setDataNascimento(data);
        try {
            if (idSelecionado > 0) {
                dao.atualizar(p);
            } else {
                dao.inserir(p);
            }
            carregar();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Não gravou.\n" + ex.getMessage());
        }
    }

    /** DELETE — remove o pet selecionado, com confirmação. */
    private void excluir() {
        if (idSelecionado <= 0) {
            JOptionPane.showMessageDialog(this, "Selecione um pet na tabela.");
            return;
        }
        int ok = JOptionPane.showConfirmDialog(this, "Excluir o pet selecionado?", "Confirmar",
                JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) return;
        try {
            dao.excluir(idSelecionado);
            carregar();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Não excluiu.\n" + ex.getMessage()
                    + "\n(Pet com consultas não pode ser excluído.)");
        }
    }

    /** Converte "aaaa-mm-dd" em LocalDate. Devolve null se vazio ou inválido. */
    private LocalDate parseData(String texto) {
        if (texto.isEmpty()) return null;
        try {
            return LocalDate.parse(texto); // formato ISO padrão (2020-03-15)
        } catch (Exception e) {
            return null;
        }
    }

    /** Zera seleção e campos (combo volta para o primeiro item). */
    private void limpar() {
        idSelecionado = 0;
        tabela.clearSelection();
        campoNome.setText("");
        campoEspecie.setText("");
        campoRaca.setText("");
        campoNascimento.setText("");
        if (comboTutor.getItemCount() > 0) comboTutor.setSelectedIndex(0);
    }

    private String texto(Object o) {
        return o == null ? "" : o.toString();
    }

    /**
     * TutorRenderer — diz ao combo como DESENHAR cada item.
     * Sem isso, o combo mostraria algo como "model.Tutor@1a2b3c". Com ele,
     * mostramos o nome do tutor, mas o item guardado continua sendo o objeto
     * Tutor completo (preservando o id para a FK).
     *
     * [MOLDE] Copie este renderer trocando Tutor pela sua entidade-pai.
     */
    private static class TutorRenderer extends javax.swing.DefaultListCellRenderer {
        @Override
        public java.awt.Component getListCellRendererComponent(javax.swing.JList<?> lista, Object valor,
                int indice, boolean selecionado, boolean foco) {
            super.getListCellRendererComponent(lista, valor, indice, selecionado, foco);
            if (valor instanceof Tutor) {
                setText(((Tutor) valor).getNome());
            }
            return this;
        }
    }
}
