package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import model.Tutor;
import util.ConnectionFactory;

public class TutorDAO {

    public List<Tutor> listar(String filtroNome) {
        String sql = "SELECT id_tutor, nome, telefone, email FROM tutor "
                + "WHERE nome LIKE ? ORDER BY nome";
        List<Tutor> lista = new ArrayList<>();
        try (Connection cn = ConnectionFactory.get();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            String like = "%" + (filtroNome == null ? "" : filtroNome.trim()) + "%";
            ps.setString(1, like);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Tutor t = new Tutor();
                    t.setId(rs.getInt("id_tutor"));
                    t.setNome(rs.getString("nome"));
                    t.setTelefone(rs.getString("telefone"));
                    t.setEmail(rs.getString("email"));
                    lista.add(t);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao listar tutores: " + e.getMessage(), e);
        }
        return lista;
    }

    public void inserir(Tutor t) {
        String sql = "INSERT INTO tutor (nome, telefone, email) VALUES (?, ?, ?)";
        try (Connection cn = ConnectionFactory.get();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, t.getNome());
            ps.setString(2, t.getTelefone());
            ps.setString(3, t.getEmail());
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao inserir tutor: " + e.getMessage(), e);
        }
    }
}
