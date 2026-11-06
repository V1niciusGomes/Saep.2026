package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import model.ItemB;
import util.ConnectionFactory;

/**
 * ItemBDAO — CRUD simples do segundo cadastro (ItemB).
 * É idêntico em estrutura ao ItemDAO; existe só para dar um "segundo pai"
 * à entidade de ligação. Bom exemplo de como o molde de CRUD simples se repete.
 *
 * [MOLDE] Mesmas instruções do ItemDAO: copie e troque tabela/colunas.
 */
public class ItemBDAO {

    /** READ — lista por nome (filtro vazio = todos). */
    public List<ItemB> listar(String filtro) {
        String sql = "SELECT id_item_b, nome, descricao FROM item_b "
                + "WHERE nome LIKE ? ORDER BY nome";
        List<ItemB> lista = new ArrayList<>();
        try (Connection cn = ConnectionFactory.get();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            String like = "%" + (filtro == null ? "" : filtro.trim()) + "%";
            ps.setString(1, like);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ItemB b = new ItemB();
                    b.setId(rs.getInt("id_item_b"));
                    b.setNome(rs.getString("nome"));
                    b.setDescricao(rs.getString("descricao"));
                    lista.add(b);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao listar itens B: " + e.getMessage(), e);
        }
        return lista;
    }

    /** CREATE — insere um novo item B. */
    public void inserir(ItemB b) {
        String sql = "INSERT INTO item_b (nome, descricao) VALUES (?, ?)";
        try (Connection cn = ConnectionFactory.get();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, b.getNome());
            ps.setString(2, b.getDescricao());
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao inserir item B: " + e.getMessage(), e);
        }
    }

    /** UPDATE — altera o item B pelo id. */
    public void atualizar(ItemB b) {
        String sql = "UPDATE item_b SET nome=?, descricao=? WHERE id_item_b=?";
        try (Connection cn = ConnectionFactory.get();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, b.getNome());
            ps.setString(2, b.getDescricao());
            ps.setInt(3, b.getId());
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao atualizar item B: " + e.getMessage(), e);
        }
    }

    /** DELETE — remove o item B pelo id. */
    public void excluir(int id) {
        String sql = "DELETE FROM item_b WHERE id_item_b=?";
        try (Connection cn = ConnectionFactory.get();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            // Obs.: se o item B estiver em alguma ligação, a FK impede apagar.
        } catch (Exception e) {
            throw new RuntimeException("Erro ao excluir item B: " + e.getMessage(), e);
        }
    }
}
