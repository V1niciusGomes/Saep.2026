package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import model.Peca;
import util.ConnectionFactory;

/**
 * PecaDAO — CRUD de peça (molde Item: cadastro simples).
 */
public class PecaDAO {

    public List<Peca> listar(String filtro) {
        String sql = "SELECT id_peca, nome, codigo, preco, estoque FROM peca "
                   + "WHERE nome LIKE ? ORDER BY nome";
        List<Peca> lista = new ArrayList<>();
        try (Connection cn = ConnectionFactory.get();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, "%" + (filtro == null ? "" : filtro.trim()) + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Peca p = new Peca();
                    p.setId(rs.getInt("id_peca"));
                    p.setNome(rs.getString("nome"));
                    p.setCodigo(rs.getString("codigo"));
                    p.setPreco(rs.getDouble("preco"));
                    p.setEstoque(rs.getInt("estoque"));
                    lista.add(p);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao listar peças: " + e.getMessage(), e);
        }
        return lista;
    }

    public void inserir(Peca p) {
        String sql = "INSERT INTO peca (nome, codigo, preco, estoque) VALUES (?, ?, ?, ?)";
        try (Connection cn = ConnectionFactory.get();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, p.getNome());
            ps.setString(2, p.getCodigo());
            ps.setDouble(3, p.getPreco());
            ps.setInt(4, p.getEstoque());
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao inserir peça: " + e.getMessage(), e);
        }
    }

    public void atualizar(Peca p) {
        String sql = "UPDATE peca SET nome=?, codigo=?, preco=?, estoque=? WHERE id_peca=?";
        try (Connection cn = ConnectionFactory.get();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, p.getNome());
            ps.setString(2, p.getCodigo());
            ps.setDouble(3, p.getPreco());
            ps.setInt(4, p.getEstoque());
            ps.setInt(5, p.getId());
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao atualizar peça: " + e.getMessage(), e);
        }
    }

    public void excluir(int id) {
        String sql = "DELETE FROM peca WHERE id_peca=?";
        try (Connection cn = ConnectionFactory.get();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao excluir peça: " + e.getMessage(), e);
        }
    }
}
