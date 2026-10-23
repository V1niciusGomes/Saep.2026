package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import model.Consulta;
import util.ConnectionFactory;

public class ConsultaDAO {

    public List<Consulta> listar(String filtro) {
        String sql = "SELECT c.id_consulta, c.id_pet, c.id_veterinario, c.data_hora, c.motivo, c.observacao, "
                + "p.nome AS nome_pet, v.nome AS nome_vet "
                + "FROM consulta c "
                + "JOIN pet p ON p.id_pet = c.id_pet "
                + "JOIN veterinario v ON v.id_veterinario = c.id_veterinario "
                + "WHERE p.nome LIKE ? OR v.nome LIKE ? OR DATE(c.data_hora) LIKE ? "
                + "ORDER BY c.data_hora";
        List<Consulta> lista = new ArrayList<>();
        try (Connection cn = ConnectionFactory.get();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            String like = "%" + (filtro == null ? "" : filtro.trim()) + "%";
            ps.setString(1, like);
            ps.setString(2, like);
            ps.setString(3, like);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Consulta c = new Consulta();
                    c.setId(rs.getInt("id_consulta"));
                    c.setIdPet(rs.getInt("id_pet"));
                    c.setIdVeterinario(rs.getInt("id_veterinario"));
                    if (rs.getTimestamp("data_hora") != null) {
                        c.setDataHora(rs.getTimestamp("data_hora").toLocalDateTime());
                    }
                    c.setMotivo(rs.getString("motivo"));
                    c.setObservacao(rs.getString("observacao"));
                    c.setNomePet(rs.getString("nome_pet"));
                    c.setNomeVet(rs.getString("nome_vet"));
                    lista.add(c);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao listar consultas: " + e.getMessage(), e);
        }
        return lista;
    }

    public void inserir(Consulta c) {
        String sql = "INSERT INTO consulta (id_pet, id_veterinario, data_hora, motivo, observacao) VALUES (?, ?, ?, ?, ?)";
        try (Connection cn = ConnectionFactory.get();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, c.getIdPet());
            ps.setInt(2, c.getIdVeterinario());
            ps.setObject(3, c.getDataHora());
            ps.setString(4, c.getMotivo());
            ps.setString(5, c.getObservacao());
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao inserir consulta: " + e.getMessage(), e);
        }
    }

    public void atualizar(Consulta c) {
        String sql = "UPDATE consulta SET id_pet=?, id_veterinario=?, data_hora=?, motivo=?, observacao=? WHERE id_consulta=?";
        try (Connection cn = ConnectionFactory.get();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, c.getIdPet());
            ps.setInt(2, c.getIdVeterinario());
            ps.setObject(3, c.getDataHora());
            ps.setString(4, c.getMotivo());
            ps.setString(5, c.getObservacao());
            ps.setInt(6, c.getId());
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao atualizar consulta: " + e.getMessage(), e);
        }
    }

    public void excluir(int id) {
        String sql = "DELETE FROM consulta WHERE id_consulta=?";
        try (Connection cn = ConnectionFactory.get();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao excluir consulta: " + e.getMessage(), e);
        }
    }
}
