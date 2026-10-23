package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

import dao.ConsultaDAO;
import dao.PetDAO;
import dao.VeterinarioDAO;
import model.Consulta;
import model.Pet;
import model.Veterinario;

public class ConsultaView extends JFrame {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final ConsultaDAO dao = new ConsultaDAO();
    private final PetDAO petDao = new PetDAO();
    private final VeterinarioDAO vetDao = new VeterinarioDAO();
    private final JTextField campoBusca = new JTextField(16);
    private final JComboBox<Pet> comboPet = new JComboBox<>();
    private final JComboBox<Veterinario> comboVet = new JComboBox<>();
    private final JTextField campoDataHora = new JTextField();
    private final JTextField campoMotivo = new JTextField();
    private final JTextField campoObservacao = new JTextField();
    private int idSelecionado = 0;
    private final DefaultTableModel modelo = new DefaultTableModel(
            new String[] { "ID", "Data/Hora", "Pet", "Veterinário", "Motivo", "Observação" }, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable tabela = new JTable(modelo);

    public ConsultaView() {
        setTitle("Consultas");
        setSize(900, 560);
        setLocationRelativeTo(null);

        comboPet.setRenderer(new PetRenderer());
        comboVet.setRenderer(new VetRenderer());

        JPanel busca = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.addActionListener(e -> carregar());
        busca.add(new JLabel("Pet, vet ou data"));
        busca.add(campoBusca);
        busca.add(btnBuscar);

        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.getSelectionModel().addListSelectionListener(e -> preencherSelecionado());

        JPanel form = new JPanel(new GridLayout(5, 2, 8, 8));
        form.setBorder(BorderFactory.createTitledBorder("Dados da consulta"));
        form.add(new JLabel("Pet"));
        form.add(comboPet);
        form.add(new JLabel("Veterinário"));
        form.add(comboVet);
        form.add(new JLabel("Data/Hora (aaaa-mm-dd hh:mm)"));
        form.add(campoDataHora);
        form.add(new JLabel("Motivo"));
        form.add(campoMotivo);
        form.add(new JLabel("Observação"));
        form.add(campoObservacao);

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

        JLabel faixa = new JLabel("  Precisa de pet e de veterinário existentes (FK). A lista mostra sempre em ordem de data.");
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
        carregarCombos();
        carregar();
    }

    private void carregarCombos() {
        DefaultComboBoxModel<Pet> pm = new DefaultComboBoxModel<>();
        for (Pet p : petDao.listar("")) {
            pm.addElement(p);
        }
        comboPet.setModel(pm);
        comboPet.setRenderer(new PetRenderer());

        DefaultComboBoxModel<Veterinario> vm = new DefaultComboBoxModel<>();
        for (Veterinario v : vetDao.listar("")) {
            vm.addElement(v);
        }
        comboVet.setModel(vm);
        comboVet.setRenderer(new VetRenderer());
    }

    private void carregar() {
        modelo.setRowCount(0);
        List<Consulta> lista = dao.listar(campoBusca.getText());
        for (Consulta c : lista) {
            modelo.addRow(new Object[] { c.getId(), formatar(c.getDataHora()), c.getNomePet(),
                    c.getNomeVet(), c.getMotivo(), c.getObservacao() });
        }
        limpar();
    }

    private void preencherSelecionado() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) return;
        idSelecionado = (int) modelo.getValueAt(linha, 0);
        campoDataHora.setText(texto(modelo.getValueAt(linha, 1)));
        selecionarPetPorNome(texto(modelo.getValueAt(linha, 2)));
        selecionarVetPorNome(texto(modelo.getValueAt(linha, 3)));
        campoMotivo.setText(texto(modelo.getValueAt(linha, 4)));
        campoObservacao.setText(texto(modelo.getValueAt(linha, 5)));
    }

    private void selecionarPetPorNome(String nome) {
        for (int i = 0; i < comboPet.getItemCount(); i++) {
            if (comboPet.getItemAt(i).getNome().equals(nome)) {
                comboPet.setSelectedIndex(i);
                return;
            }
        }
    }

    private void selecionarVetPorNome(String nome) {
        for (int i = 0; i < comboVet.getItemCount(); i++) {
            if (comboVet.getItemAt(i).getNome().equals(nome)) {
                comboVet.setSelectedIndex(i);
                return;
            }
        }
    }

    private void salvar() {
        Pet pet = (Pet) comboPet.getSelectedItem();
        Veterinario vet = (Veterinario) comboVet.getSelectedItem();
        if (pet == null || vet == null) {
            JOptionPane.showMessageDialog(this, "Escolha um pet e um veterinário. Cadastre-os antes se as listas estiverem vazias.");
            return;
        }
        LocalDateTime dataHora = parseDataHora(campoDataHora.getText().trim());
        if (dataHora == null) {
            JOptionPane.showMessageDialog(this, "Data/hora inválida. Use o formato aaaa-mm-dd hh:mm.");
            return;
        }
        Consulta c = new Consulta();
        c.setId(idSelecionado);
        c.setIdPet(pet.getId());
        c.setIdVeterinario(vet.getId());
        c.setDataHora(dataHora);
        c.setMotivo(campoMotivo.getText().trim());
        c.setObservacao(campoObservacao.getText().trim());
        try {
            if (idSelecionado > 0) {
                dao.atualizar(c);
            } else {
                dao.inserir(c);
            }
            carregar();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Não gravou.\n" + ex.getMessage());
        }
    }

    private void excluir() {
        if (idSelecionado <= 0) {
            JOptionPane.showMessageDialog(this, "Selecione uma consulta na tabela.");
            return;
        }
        int ok = JOptionPane.showConfirmDialog(this, "Excluir a consulta selecionada?", "Confirmar",
                JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) return;
        try {
            dao.excluir(idSelecionado);
            carregar();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Não excluiu.\n" + ex.getMessage());
        }
    }

    private LocalDateTime parseDataHora(String texto) {
        if (texto.isEmpty()) return null;
        try {
            return LocalDateTime.parse(texto, FMT);
        } catch (Exception e) {
            return null;
        }
    }

    private String formatar(LocalDateTime dataHora) {
        return dataHora == null ? "" : dataHora.format(FMT);
    }

    private void limpar() {
        idSelecionado = 0;
        tabela.clearSelection();
        campoDataHora.setText("");
        campoMotivo.setText("");
        campoObservacao.setText("");
        if (comboPet.getItemCount() > 0) comboPet.setSelectedIndex(0);
        if (comboVet.getItemCount() > 0) comboVet.setSelectedIndex(0);
    }

    private String texto(Object o) {
        return o == null ? "" : o.toString();
    }

    private static class PetRenderer extends javax.swing.DefaultListCellRenderer {
        @Override
        public java.awt.Component getListCellRendererComponent(javax.swing.JList<?> lista, Object valor,
                int indice, boolean selecionado, boolean foco) {
            super.getListCellRendererComponent(lista, valor, indice, selecionado, foco);
            if (valor instanceof Pet) {
                setText(((Pet) valor).getNome());
            }
            return this;
        }
    }

    private static class VetRenderer extends javax.swing.DefaultListCellRenderer {
        @Override
        public java.awt.Component getListCellRendererComponent(javax.swing.JList<?> lista, Object valor,
                int indice, boolean selecionado, boolean foco) {
            super.getListCellRendererComponent(lista, valor, indice, selecionado, foco);
            if (valor instanceof Veterinario) {
                setText(((Veterinario) valor).getNome());
            }
            return this;
        }
    }
}
