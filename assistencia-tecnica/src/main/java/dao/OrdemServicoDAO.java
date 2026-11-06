package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import model.OrdemServico;
import util.ConnectionFactory;

/**
 * OrdemServicoDAO — a entidade CENTRAL (molde ItemLigacao com TRÊS pais).
 * O listar faz TRÊS JOINs (cliente, tecnico, aparelho) e o inserir valida as 3 FKs.
 * A data é DATETIME no banco e LocalDateTime no Java (ponte via Timestamp).
 */
public class OrdemServicoDAO {

    public List<OrdemServico> listar(String filtro) {
        String sql = "SELECT o.id_ordem_servico, o.id_cliente, o.id_tecnico, o.id_aparelho, "
                   + "       o.data_abertura, o.status, o.valor_mao_obra, o.defeito_relatado, "
                   + "       c.nome AS nome_cliente, t.nome AS nome_tecnico, "
                   + "       CONCAT(a.tipo, ' ', COALESCE(a.marca,''), ' ', COALESCE(a.modelo,'')) AS desc_aparelho "
                   + "FROM ordem_servico o "
                   + "JOIN cliente  c ON c.id_cliente  = o.id_cliente "
                   + "JOIN tecnico  t ON t.id_tecnico  = o.id_tecnico "
                   + "JOIN aparelho a ON a.id_aparelho = o.id_aparelho "
                   + "WHERE c.nome LIKE ? OR t.nome LIKE ? OR o.status LIKE ? "
                   + "ORDER BY o.data_abertura DESC";
        List<OrdemServico> lista = new ArrayList<>();
        try (Connection cn = ConnectionFactory.get();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            String like = "%" + (filtro == null ? "" : filtro.trim()) + "%";
            ps.setString(1, like);
            ps.setString(2, like);
            ps.setString(3, like);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    OrdemServico o = new OrdemServico();
                    o.setId(rs.getInt("id_ordem_servico"));
                    o.setIdCliente(rs.getInt("id_cliente"));
                    o.setIdTecnico(rs.getInt("id_tecnico"));
                    o.setIdAparelho(rs.getInt("id_aparelho"));
                    Timestamp ts = rs.getTimestamp("data_abertura");
                    if (ts != null) o.setDataAbertura(ts.toLocalDateTime());
                    o.setStatus(rs.getString("status"));
                    o.setValorMaoObra(rs.getDouble("valor_mao_obra"));
                    o.setDefeitoRelatado(rs.getString("defeito_relatado"));
                    o.setNomeCliente(rs.getString("nome_cliente"));
                    o.setNomeTecnico(rs.getString("nome_tecnico"));
                    o.setDescricaoAparelho(rs.getString("desc_aparelho"));
                    lista.add(o);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao listar ordens de serviço: " + e.getMessage(), e);
        }
        return lista;
    }

    public void inserir(OrdemServico o) {
        if (o.getIdCliente() <= 0)  throw new IllegalArgumentException("OS sem cliente.");
        if (o.getIdTecnico() <= 0)  throw new IllegalArgumentException("OS sem técnico.");
        if (o.getIdAparelho() <= 0) throw new IllegalArgumentException("OS sem aparelho.");
        String sql = "INSERT INTO ordem_servico "
                   + "(id_cliente, id_tecnico, id_aparelho, data_abertura, status, valor_mao_obra, defeito_relatado) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection cn = ConnectionFactory.get();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, o.getIdCliente());
            ps.setInt(2, o.getIdTecnico());
            ps.setInt(3, o.getIdAparelho());
            ps.setTimestamp(4, o.getDataAbertura() == null ? null : Timestamp.valueOf(o.getDataAbertura()));
            ps.setString(5, o.getStatus());
            ps.setDouble(6, o.getValorMaoObra());
            ps.setString(7, o.getDefeitoRelatado());
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao inserir ordem de serviço: " + e.getMessage(), e);
        }
    }

    public void atualizar(OrdemServico o) {
        String sql = "UPDATE ordem_servico SET id_cliente=?, id_tecnico=?, id_aparelho=?, "
                   + "data_abertura=?, status=?, valor_mao_obra=?, defeito_relatado=? "
                   + "WHERE id_ordem_servico=?";
        try (Connection cn = ConnectionFactory.get();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, o.getIdCliente());
            ps.setInt(2, o.getIdTecnico());
            ps.setInt(3, o.getIdAparelho());
            ps.setTimestamp(4, o.getDataAbertura() == null ? null : Timestamp.valueOf(o.getDataAbertura()));
            ps.setString(5, o.getStatus());
            ps.setDouble(6, o.getValorMaoObra());
            ps.setString(7, o.getDefeitoRelatado());
            ps.setInt(8, o.getId());
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao atualizar ordem de serviço: " + e.getMessage(), e);
        }
    }

    public void excluir(int id) {
        String sql = "DELETE FROM ordem_servico WHERE id_ordem_servico=?";
        try (Connection cn = ConnectionFactory.get();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            // Se a OS tiver peças (ordem_peca), a FK impede a exclusão.
        } catch (Exception e) {
            throw new RuntimeException("Erro ao excluir ordem de serviço: " + e.getMessage(), e);
        }
    }
}
