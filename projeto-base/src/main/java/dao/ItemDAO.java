package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import model.Item;
import util.ConnectionFactory;

/**
 * ItemDAO — EXEMPLO-MESTRE de um DAO com CRUD completo.
 *
 * CRUD = as 4 operações básicas de qualquer cadastro:
 *   - listar    (READ)   -> SELECT
 *   - inserir   (CREATE) -> INSERT
 *   - atualizar (UPDATE) -> UPDATE
 *   - excluir   (DELETE) -> DELETE
 *
 * PADRÃO repetido em TODOS os métodos (decore este esqueleto):
 *   1. monta o SQL com "?" nos valores;
 *   2. abre conexão + statement no try-with-resources (fecha sozinho);
 *   3. preenche os "?" com ps.setXxx(posição, valor);
 *   4. executa (executeQuery para SELECT, executeUpdate para gravação);
 *   5. em erro, lança RuntimeException com mensagem clara.
 *
 * [MOLDE] Para um cadastro simples novo, copie este DAO inteiro e:
 *   - troque Item -> SuaEntidade e item/id_item -> sua_tabela/sua_pk;
 *   - ajuste a lista de colunas nos SELECT/INSERT/UPDATE.
 */
public class ItemDAO {

    /** READ — lista itens cujo nome contém o filtro (ou todos, se filtro vazio). */
    public List<Item> listar(String filtro) {
        String sql = "SELECT id_item, nome, descricao, codigo FROM item "
                + "WHERE nome LIKE ? ORDER BY nome";
        List<Item> lista = new ArrayList<>();
        try (Connection cn = ConnectionFactory.get();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            // LIKE com % dos dois lados = "contém". filtro null vira "" (lista tudo).
            String like = "%" + (filtro == null ? "" : filtro.trim()) + "%";
            ps.setString(1, like);

            try (ResultSet rs = ps.executeQuery()) {
                // Para cada linha retornada, cria um Item e adiciona na lista.
                while (rs.next()) {
                    Item i = new Item();
                    i.setId(rs.getInt("id_item"));
                    i.setNome(rs.getString("nome"));
                    i.setDescricao(rs.getString("descricao"));
                    i.setCodigo(rs.getString("codigo"));
                    lista.add(i);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao listar itens: " + e.getMessage(), e);
        }
        return lista;
    }

    /** CREATE — insere um novo item. */
    public void inserir(Item i) {
        String sql = "INSERT INTO item (nome, descricao, codigo) VALUES (?, ?, ?)";
        try (Connection cn = ConnectionFactory.get();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, i.getNome());
            ps.setString(2, i.getDescricao());
            ps.setString(3, i.getCodigo());
            // executeUpdate() = comando que ALTERA o banco (INSERT/UPDATE/DELETE).
            ps.executeUpdate();
        } catch (Exception e) {
            // Cai aqui também quando o "codigo" já existe (violação de UNIQUE).
            throw new RuntimeException("Erro ao inserir item: " + e.getMessage(), e);
        }
    }

    /** UPDATE — altera o item cujo id_item casa com i.getId(). */
    public void atualizar(Item i) {
        String sql = "UPDATE item SET nome=?, descricao=?, codigo=? WHERE id_item=?";
        try (Connection cn = ConnectionFactory.get();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, i.getNome());
            ps.setString(2, i.getDescricao());
            ps.setString(3, i.getCodigo());
            ps.setInt(4, i.getId());  // o WHERE: diz QUAL linha atualizar
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao atualizar item: " + e.getMessage(), e);
        }
    }

    /** DELETE — remove o item pelo id. */
    public void excluir(int id) {
        String sql = "DELETE FROM item WHERE id_item=?";
        try (Connection cn = ConnectionFactory.get();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            // Obs.: se o item tiver filhos, o banco recusa a exclusão (FK).
        } catch (Exception e) {
            throw new RuntimeException("Erro ao excluir item: " + e.getMessage(), e);
        }
    }
}
