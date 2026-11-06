package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import model.ItemFilho;
import util.ConnectionFactory;

/**
 * ItemFilhoDAO — CRUD de uma entidade que PERTENCE A outra (filho tem pai).
 * Mostra dois recursos a mais do molde: JOIN e regra de negócio.
 *
 * JOIN no listar:
 *   "FROM item_filho f JOIN item i ON i.id_item = f.id_item" traz, em uma
 *   única consulta, o nome do pai junto com o filho. Por isso o SELECT pega
 *   "i.nome AS nome_item" e guardamos em f.setNomeItem(...). Esse campo é
 *   só exibição (não é coluna da tabela item_filho).
 *
 * Regra de negócio no inserir:
 *   antes de gravar, recusamos filho sem pai (idItem <= 0) com uma mensagem
 *   clara. O banco também recusaria pela FK, mas validar antes dá erro melhor.
 *
 * [MOLDE] Para "entidade filha" (ItemDoPedido->Pedido, Telefone->Cliente):
 *   - no listar, faça o JOIN com a tabela pai e traga o nome dela;
 *   - repita a validação do id do pai no inserir/atualizar.
 */
public class ItemFilhoDAO {

    /** READ — lista filhos; o filtro casa tanto no nome do filho quanto no do pai. */
    public List<ItemFilho> listar(String filtro) {
        String sql = "SELECT f.id_item_filho, f.nome, f.valor, f.id_item, i.nome AS nome_item "
                + "FROM item_filho f JOIN item i ON i.id_item = f.id_item "
                + "WHERE f.nome LIKE ? OR i.nome LIKE ? ORDER BY f.nome";
        List<ItemFilho> lista = new ArrayList<>();
        try (Connection cn = ConnectionFactory.get();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            String like = "%" + (filtro == null ? "" : filtro.trim()) + "%";
            ps.setString(1, like);  // filtro no nome do filho
            ps.setString(2, like);  // mesmo filtro no nome do pai
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ItemFilho f = new ItemFilho();
                    f.setId(rs.getInt("id_item_filho"));
                    f.setNome(rs.getString("nome"));
                    f.setValor(rs.getDouble("valor"));
                    f.setIdItem(rs.getInt("id_item"));       // o id (FK) para edição
                    f.setNomeItem(rs.getString("nome_item")); // o nome (exibição) do JOIN
                    lista.add(f);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao listar filhos: " + e.getMessage(), e);
        }
        return lista;
    }

    /** CREATE — insere um filho, mas só se ele tiver um pai válido. */
    public void inserir(ItemFilho f) {
        // Regra de negócio: filho sem pai não grava. Erro claro antes do banco.
        if (f.getIdItem() <= 0) throw new IllegalArgumentException("Filho sem item (pai) não grava.");
        String sql = "INSERT INTO item_filho (nome, valor, id_item) VALUES (?, ?, ?)";
        try (Connection cn = ConnectionFactory.get();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, f.getNome());
            ps.setDouble(2, f.getValor());
            ps.setInt(3, f.getIdItem()); // a FK: liga o filho ao pai
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao inserir filho: " + e.getMessage(), e);
        }
    }

    /** UPDATE — altera o filho pelo id (inclusive pode trocar de pai). */
    public void atualizar(ItemFilho f) {
        String sql = "UPDATE item_filho SET nome=?, valor=?, id_item=? WHERE id_item_filho=?";
        try (Connection cn = ConnectionFactory.get();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, f.getNome());
            ps.setDouble(2, f.getValor());
            ps.setInt(3, f.getIdItem());
            ps.setInt(4, f.getId());
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao atualizar filho: " + e.getMessage(), e);
        }
    }

    /** DELETE — remove o filho pelo id. */
    public void excluir(int id) {
        String sql = "DELETE FROM item_filho WHERE id_item_filho=?";
        try (Connection cn = ConnectionFactory.get();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao excluir filho: " + e.getMessage(), e);
        }
    }
}
