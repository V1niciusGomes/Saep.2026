package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import model.Veterinario;
import util.ConnectionFactory;

public class VeterinarioDAO {

    public List<Veterinario> listar(String filtro) {
        String sql = "SELECT id_veterinario, nome, crmv, especialidade FROM veterinario "
                + "WHERE nome LIKE ? ORDER BY nome";
        List<Veterinario> lista = new ArrayList<>();
        try (Connection cn = ConnectionFactory.get();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            String like = "%" + (filtro == null ? "" : filtro.trim()) + "%";
            ps.setString(1, like);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Veterinario v = new Veterinario();
                    v.setId(rs.getInt("id_veterinario"));
                    v.setNome(rs.getString("nome"));
                    v.setCrmv(rs.getString("crmv"));
                    v.setEspecialidade(rs.getString("especialidade"));
                    lista.add(v);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao listar veterinários: " + e.getMessage(), e);
        }
        return lista;
    }

    public void inserir(Veterinario v) {
        String sql = "INSERT INTO veterinario (nome, crmv, especialidade) VALUES (?, ?, ?)";
        try (Connection cn = ConnectionFactory.get();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, v.getNome());
            ps.setString(2, v.getCrmv());
            ps.setString(3, v.getEspecialidade());
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao inserir veterinário: " + e.getMessage(), e);
        }
    }

    public void atualizar(Veterinario v) {
        String sql = "UPDATE veterinario SET nome=?, crmv=?, especialidade=? WHERE id_veterinario=?";
        try (Connection cn = ConnectionFactory.get();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, v.getNome());
            ps.setString(2, v.getCrmv());
            ps.setString(3, v.getEspecialidade());
            ps.setInt(4, v.getId());
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao atualizar veterinário: " + e.getMessage(), e);
        }
    }

    public void excluir(int id) {
        String sql = "DELETE FROM veterinario WHERE id_veterinario=?";
        try (Connection cn = ConnectionFactory.get();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao excluir veterinário: " + e.getMessage(), e);
        }
    }
}
