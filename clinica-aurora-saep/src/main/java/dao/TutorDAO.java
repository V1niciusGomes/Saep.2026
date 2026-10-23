package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import model.Tutor;
import util.ConnectionFactory;

public class TutorDAO {
    private static final String KEY = "SENAI2026";

    public List<Tutor> listar(String filtro) {
        String sql = "SELECT id_tutor, nome, telefone, email, "
                + "CAST(AES_DECRYPT(cpf, ?) AS CHAR) AS cpf "
                + "FROM tutor WHERE nome LIKE ? ORDER BY nome";
        List<Tutor> lista = new ArrayList<>();
        try (Connection cn = ConnectionFactory.get();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            String like = "%" + (filtro == null ? "" : filtro.trim()) + "%";
            ps.setString(1, KEY);
            ps.setString(2, like);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Tutor t = new Tutor();
                    t.setId(rs.getInt("id_tutor"));
                    t.setNome(rs.getString("nome"));
                    t.setTelefone(rs.getString("telefone"));
                    t.setEmail(rs.getString("email"));
                    t.setCpf(rs.getString("cpf"));
                    lista.add(t);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao listar tutores: " + e.getMessage(), e);
        }
        return lista;
    }

    public void inserir(Tutor t) {
        String sql = "INSERT INTO tutor (nome, telefone, email, cpf) VALUES (?, ?, ?, AES_ENCRYPT(?, ?))";
        try (Connection cn = ConnectionFactory.get();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, t.getNome());
            ps.setString(2, t.getTelefone());
            ps.setString(3, t.getEmail());
            ps.setString(4, t.getCpf() == null ? "" : t.getCpf());
            ps.setString(5, KEY);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao inserir tutor: " + e.getMessage(), e);
        }
    }

    public void atualizar(Tutor t) {
        String sql = "UPDATE tutor SET nome=?, telefone=?, email=?, cpf=AES_ENCRYPT(?, ?) WHERE id_tutor=?";
        try (Connection cn = ConnectionFactory.get();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, t.getNome());
            ps.setString(2, t.getTelefone());
            ps.setString(3, t.getEmail());
            ps.setString(4, t.getCpf() == null ? "" : t.getCpf());
            ps.setString(5, KEY);
            ps.setInt(6, t.getId());
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao atualizar tutor: " + e.getMessage(), e);
        }
    }

    public void excluir(int id) {
        String sql = "DELETE FROM tutor WHERE id_tutor=?";
        try (Connection cn = ConnectionFactory.get();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao excluir tutor: " + e.getMessage(), e);
        }
    }
}
