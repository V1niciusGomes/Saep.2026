package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import model.Pet;
import util.ConnectionFactory;

public class PetDAO {

    public List<Pet> listar(String filtro) {
        String sql = "SELECT p.id_pet, p.nome, p.especie, p.raca, p.data_nascimento, p.id_tutor, t.nome AS nome_tutor "
                + "FROM pet p JOIN tutor t ON t.id_tutor = p.id_tutor "
                + "WHERE p.nome LIKE ? OR t.nome LIKE ? ORDER BY p.nome";
        List<Pet> lista = new ArrayList<>();
        try (Connection cn = ConnectionFactory.get();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            String like = "%" + (filtro == null ? "" : filtro.trim()) + "%";
            ps.setString(1, like);
            ps.setString(2, like);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Pet p = new Pet();
                    p.setId(rs.getInt("id_pet"));
                    p.setNome(rs.getString("nome"));
                    p.setEspecie(rs.getString("especie"));
                    p.setRaca(rs.getString("raca"));
                    if (rs.getDate("data_nascimento") != null) {
                        p.setDataNascimento(rs.getDate("data_nascimento").toLocalDate());
                    }
                    p.setIdTutor(rs.getInt("id_tutor"));
                    p.setNomeTutor(rs.getString("nome_tutor"));
                    lista.add(p);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao listar pets: " + e.getMessage(), e);
        }
        return lista;
    }

    public void inserir(Pet p) {
        if (p.getIdTutor() <= 0) throw new IllegalArgumentException("Pet sem tutor não grava.");
        String sql = "INSERT INTO pet (nome, especie, raca, data_nascimento, id_tutor) VALUES (?, ?, ?, ?, ?)";
        try (Connection cn = ConnectionFactory.get();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, p.getNome());
            ps.setString(2, p.getEspecie());
            ps.setString(3, p.getRaca());
            ps.setObject(4, p.getDataNascimento());
            ps.setInt(5, p.getIdTutor());
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao inserir pet: " + e.getMessage(), e);
        }
    }

    public void atualizar(Pet p) {
        String sql = "UPDATE pet SET nome=?, especie=?, raca=?, data_nascimento=?, id_tutor=? WHERE id_pet=?";
        try (Connection cn = ConnectionFactory.get();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, p.getNome());
            ps.setString(2, p.getEspecie());
            ps.setString(3, p.getRaca());
            ps.setObject(4, p.getDataNascimento());
            ps.setInt(5, p.getIdTutor());
            ps.setInt(6, p.getId());
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao atualizar pet: " + e.getMessage(), e);
        }
    }

    public void excluir(int id) {
        String sql = "DELETE FROM pet WHERE id_pet=?";
        try (Connection cn = ConnectionFactory.get();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao excluir pet: " + e.getMessage(), e);
        }
    }
}
