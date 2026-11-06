package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import model.Tecnico;
import util.ConnectionFactory;

/**
 * TecnicoDAO — CRUD de técnico (molde Item: cadastro simples).
 */
public class TecnicoDAO {

    public List<Tecnico> listar(String filtro) {
        String sql = "SELECT id_tecnico, nome, especialidade, telefone FROM tecnico "
                   + "WHERE nome LIKE ? ORDER BY nome";
        List<Tecnico> lista = new ArrayList<>();
        try (Connection cn = ConnectionFactory.get();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, "%" + (filtro == null ? "" : filtro.trim()) + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Tecnico t = new Tecnico();
                    t.setId(rs.getInt("id_tecnico"));
                    t.setNome(rs.getString("nome"));
                    t.setEspecialidade(rs.getString("especialidade"));
                    t.setTelefone(rs.getString("telefone"));
                    lista.add(t);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao listar técnicos: " + e.getMessage(), e);
        }
        return lista;
    }

    public void inserir(Tecnico t) {
        String sql = "INSERT INTO tecnico (nome, especialidade, telefone) VALUES (?, ?, ?)";
        try (Connection cn = ConnectionFactory.get();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, t.getNome());
            ps.setString(2, t.getEspecialidade());
            ps.setString(3, t.getTelefone());
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao inserir técnico: " + e.getMessage(), e);
        }
    }

    public void atualizar(Tecnico t) {
        String sql = "UPDATE tecnico SET nome=?, especialidade=?, telefone=? WHERE id_tecnico=?";
        try (Connection cn = ConnectionFactory.get();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, t.getNome());
            ps.setString(2, t.getEspecialidade());
            ps.setString(3, t.getTelefone());
            ps.setInt(4, t.getId());
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao atualizar técnico: " + e.getMessage(), e);
        }
    }

    public void excluir(int id) {
        String sql = "DELETE FROM tecnico WHERE id_tecnico=?";
        try (Connection cn = ConnectionFactory.get();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao excluir técnico: " + e.getMessage(), e);
        }
    }
}
