package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import model.Aparelho;
import util.ConnectionFactory;

/**
 * AparelhoDAO — CRUD de aparelho (molde ItemFilho: pertence a um cliente).
 * O listar faz JOIN com cliente para trazer o nome do dono.
 */
public class AparelhoDAO {

    public List<Aparelho> listar(String filtro) {
        String sql = "SELECT a.id_aparelho, a.tipo, a.marca, a.modelo, a.numero_serie, "
                   + "       a.id_cliente, c.nome AS nome_cliente "
                   + "FROM aparelho a JOIN cliente c ON c.id_cliente = a.id_cliente "
                   + "WHERE a.tipo LIKE ? OR a.marca LIKE ? OR c.nome LIKE ? "
                   + "ORDER BY c.nome, a.tipo";
        List<Aparelho> lista = new ArrayList<>();
        try (Connection cn = ConnectionFactory.get();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            String like = "%" + (filtro == null ? "" : filtro.trim()) + "%";
            ps.setString(1, like);
            ps.setString(2, like);
            ps.setString(3, like);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Aparelho a = new Aparelho();
                    a.setId(rs.getInt("id_aparelho"));
                    a.setTipo(rs.getString("tipo"));
                    a.setMarca(rs.getString("marca"));
                    a.setModelo(rs.getString("modelo"));
                    a.setNumeroSerie(rs.getString("numero_serie"));
                    a.setIdCliente(rs.getInt("id_cliente"));
                    a.setNomeCliente(rs.getString("nome_cliente"));
                    lista.add(a);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao listar aparelhos: " + e.getMessage(), e);
        }
        return lista;
    }

    public void inserir(Aparelho a) {
        if (a.getIdCliente() <= 0) throw new IllegalArgumentException("Aparelho sem cliente não grava.");
        String sql = "INSERT INTO aparelho (tipo, marca, modelo, numero_serie, id_cliente) "
                   + "VALUES (?, ?, ?, ?, ?)";
        try (Connection cn = ConnectionFactory.get();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, a.getTipo());
            ps.setString(2, a.getMarca());
            ps.setString(3, a.getModelo());
            ps.setString(4, a.getNumeroSerie());
            ps.setInt(5, a.getIdCliente());
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao inserir aparelho: " + e.getMessage(), e);
        }
    }

    public void atualizar(Aparelho a) {
        String sql = "UPDATE aparelho SET tipo=?, marca=?, modelo=?, numero_serie=?, id_cliente=? "
                   + "WHERE id_aparelho=?";
        try (Connection cn = ConnectionFactory.get();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, a.getTipo());
            ps.setString(2, a.getMarca());
            ps.setString(3, a.getModelo());
            ps.setString(4, a.getNumeroSerie());
            ps.setInt(5, a.getIdCliente());
            ps.setInt(6, a.getId());
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao atualizar aparelho: " + e.getMessage(), e);
        }
    }

    public void excluir(int id) {
        String sql = "DELETE FROM aparelho WHERE id_aparelho=?";
        try (Connection cn = ConnectionFactory.get();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            // Se o aparelho tiver OS, a FK impede a exclusão.
        } catch (Exception e) {
            throw new RuntimeException("Erro ao excluir aparelho: " + e.getMessage(), e);
        }
    }
}
