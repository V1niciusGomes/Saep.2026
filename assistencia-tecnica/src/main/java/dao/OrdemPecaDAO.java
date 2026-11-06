package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import model.OrdemPeca;
import util.ConnectionFactory;

/**
 * OrdemPecaDAO — peças usadas em cada OS (molde ItemLigacao / muitos-para-muitos).
 * Liga ordem_servico + peca. O listar faz JOIN com as duas para exibir nomes.
 */
public class OrdemPecaDAO {

    public List<OrdemPeca> listar(String filtro) {
        String sql = "SELECT op.id_ordem_peca, op.id_ordem_servico, op.id_peca, op.quantidade, "
                   + "       CONCAT('OS #', o.id_ordem_servico, ' - ', c.nome) AS descricao_ordem, "
                   + "       p.nome AS nome_peca "
                   + "FROM ordem_peca op "
                   + "JOIN ordem_servico o ON o.id_ordem_servico = op.id_ordem_servico "
                   + "JOIN cliente c       ON c.id_cliente       = o.id_cliente "
                   + "JOIN peca p          ON p.id_peca          = op.id_peca "
                   + "WHERE p.nome LIKE ? OR c.nome LIKE ? "
                   + "ORDER BY op.id_ordem_servico";
        List<OrdemPeca> lista = new ArrayList<>();
        try (Connection cn = ConnectionFactory.get();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            String like = "%" + (filtro == null ? "" : filtro.trim()) + "%";
            ps.setString(1, like);
            ps.setString(2, like);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    OrdemPeca op = new OrdemPeca();
                    op.setId(rs.getInt("id_ordem_peca"));
                    op.setIdOrdemServico(rs.getInt("id_ordem_servico"));
                    op.setIdPeca(rs.getInt("id_peca"));
                    op.setQuantidade(rs.getInt("quantidade"));
                    op.setDescricaoOrdem(rs.getString("descricao_ordem"));
                    op.setNomePeca(rs.getString("nome_peca"));
                    lista.add(op);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao listar peças da OS: " + e.getMessage(), e);
        }
        return lista;
    }

    public void inserir(OrdemPeca op) {
        if (op.getIdOrdemServico() <= 0) throw new IllegalArgumentException("Selecione a ordem de serviço.");
        if (op.getIdPeca() <= 0)         throw new IllegalArgumentException("Selecione a peça.");
        String sql = "INSERT INTO ordem_peca (id_ordem_servico, id_peca, quantidade) VALUES (?, ?, ?)";
        try (Connection cn = ConnectionFactory.get();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, op.getIdOrdemServico());
            ps.setInt(2, op.getIdPeca());
            ps.setInt(3, op.getQuantidade());
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao inserir peça na OS: " + e.getMessage(), e);
        }
    }

    public void atualizar(OrdemPeca op) {
        String sql = "UPDATE ordem_peca SET id_ordem_servico=?, id_peca=?, quantidade=? WHERE id_ordem_peca=?";
        try (Connection cn = ConnectionFactory.get();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, op.getIdOrdemServico());
            ps.setInt(2, op.getIdPeca());
            ps.setInt(3, op.getQuantidade());
            ps.setInt(4, op.getId());
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao atualizar peça da OS: " + e.getMessage(), e);
        }
    }

    public void excluir(int id) {
        String sql = "DELETE FROM ordem_peca WHERE id_ordem_peca=?";
        try (Connection cn = ConnectionFactory.get();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao excluir peça da OS: " + e.getMessage(), e);
        }
    }
}
