package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import model.ItemLigacao;
import util.ConnectionFactory;

/**
 * ItemLigacaoDAO — CRUD da ENTIDADE DE LIGAÇÃO (duas FKs). Molde da Ordem de Serviço.
 *
 * O QUE ESTE DAO MOSTRA DE NOVO (em relação ao ItemFilhoDAO):
 *   - JOIN DUPLO: junta a tabela item (1º pai) E a tabela item_b (2º pai)
 *     na mesma consulta, para trazer os dois nomes de uma vez.
 *   - DUAS validações de FK no inserir (os dois pais são obrigatórios).
 *   - Campo de data+hora: no banco é DATETIME; no Java é LocalDateTime.
 *     A conversão usa Timestamp (ponte entre java.time e o JDBC).
 *
 * [MOLDE] Para a sua entidade central (OrdemServico ligando Cliente + Tecnico):
 *   - troque os nomes de tabela/colunas e as FKs;
 *   - para LIGAR MAIS de duas tabelas, acrescente mais um JOIN, mais um "?"
 *     no INSERT/UPDATE e mais uma validação.
 */
public class ItemLigacaoDAO {

    /** READ — lista as ligações com os nomes dos dois pais (via JOIN duplo). */
    public List<ItemLigacao> listar(String filtro) {
        // Dois JOINs: um para cada pai. "AS nome_item" / "AS nome_item_b" são
        // apelidos usados só para leitura na tela.
        String sql = "SELECT l.id_item_ligacao, l.id_item, l.id_item_b, "
                + "       l.data_registro, l.status, l.valor, l.descricao, "
                + "       i.nome AS nome_item, b.nome AS nome_item_b "
                + "FROM item_ligacao l "
                + "JOIN item   i ON i.id_item   = l.id_item "
                + "JOIN item_b b ON b.id_item_b = l.id_item_b "
                + "WHERE i.nome LIKE ? OR b.nome LIKE ? OR l.status LIKE ? "
                + "ORDER BY l.data_registro DESC";
        List<ItemLigacao> lista = new ArrayList<>();
        try (Connection cn = ConnectionFactory.get();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            String like = "%" + (filtro == null ? "" : filtro.trim()) + "%";
            ps.setString(1, like); // filtra pelo nome do 1º pai
            ps.setString(2, like); // ou pelo nome do 2º pai
            ps.setString(3, like); // ou pelo status
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ItemLigacao l = new ItemLigacao();
                    l.setId(rs.getInt("id_item_ligacao"));
                    l.setIdItem(rs.getInt("id_item"));
                    l.setIdItemB(rs.getInt("id_item_b"));
                    // DATETIME do banco -> LocalDateTime do Java (via Timestamp).
                    Timestamp ts = rs.getTimestamp("data_registro");
                    if (ts != null) l.setDataRegistro(ts.toLocalDateTime());
                    l.setStatus(rs.getString("status"));
                    l.setValor(rs.getDouble("valor"));
                    l.setDescricao(rs.getString("descricao"));
                    l.setNomeItem(rs.getString("nome_item"));     // exibição (1º pai)
                    l.setNomeItemB(rs.getString("nome_item_b"));  // exibição (2º pai)
                    lista.add(l);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao listar ligações: " + e.getMessage(), e);
        }
        return lista;
    }

    /** CREATE — insere uma ligação; os DOIS pais são obrigatórios. */
    public void inserir(ItemLigacao l) {
        // Regra de negócio: sem os dois pais, não grava. Erro claro antes do banco.
        if (l.getIdItem() <= 0)  throw new IllegalArgumentException("Ligação sem o 1º item (pai).");
        if (l.getIdItemB() <= 0) throw new IllegalArgumentException("Ligação sem o 2º item (pai).");
        String sql = "INSERT INTO item_ligacao (id_item, id_item_b, data_registro, status, valor, descricao) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection cn = ConnectionFactory.get();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, l.getIdItem());    // FK 1
            ps.setInt(2, l.getIdItemB());   // FK 2
            // LocalDateTime -> Timestamp para gravar no DATETIME.
            ps.setTimestamp(3, l.getDataRegistro() == null ? null : Timestamp.valueOf(l.getDataRegistro()));
            ps.setString(4, l.getStatus());
            ps.setDouble(5, l.getValor());
            ps.setString(6, l.getDescricao());
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao inserir ligação: " + e.getMessage(), e);
        }
    }

    /** UPDATE — altera a ligação pelo id (pode trocar qualquer um dos pais). */
    public void atualizar(ItemLigacao l) {
        String sql = "UPDATE item_ligacao SET id_item=?, id_item_b=?, data_registro=?, "
                + "status=?, valor=?, descricao=? WHERE id_item_ligacao=?";
        try (Connection cn = ConnectionFactory.get();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, l.getIdItem());
            ps.setInt(2, l.getIdItemB());
            ps.setTimestamp(3, l.getDataRegistro() == null ? null : Timestamp.valueOf(l.getDataRegistro()));
            ps.setString(4, l.getStatus());
            ps.setDouble(5, l.getValor());
            ps.setString(6, l.getDescricao());
            ps.setInt(7, l.getId());
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao atualizar ligação: " + e.getMessage(), e);
        }
    }

    /** DELETE — remove a ligação pelo id. */
    public void excluir(int id) {
        String sql = "DELETE FROM item_ligacao WHERE id_item_ligacao=?";
        try (Connection cn = ConnectionFactory.get();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao excluir ligação: " + e.getMessage(), e);
        }
    }
}
