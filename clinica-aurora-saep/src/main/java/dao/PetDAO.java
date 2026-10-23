package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import model.Pet;
import util.ConnectionFactory;

/**
 * PetDAO — CRUD de uma entidade que PERTENCE A outra (pet tem tutor).
 * Mostra dois recursos a mais do molde: JOIN e regra de negócio.
 *
 * JOIN no listar:
 *   "FROM pet p JOIN tutor t ON t.id_tutor = p.id_tutor" traz, em uma única
 *   consulta, o nome do tutor junto com o pet. Por isso o SELECT pega
 *   "t.nome AS nome_tutor" e guardamos em p.setNomeTutor(...). Esse campo é
 *   só exibição (não é coluna da tabela pet).
 *
 * Regra de negócio no inserir:
 *   antes de gravar, recusamos pet sem tutor (idTutor <= 0) com uma mensagem
 *   clara. O banco também recusaria pela FK, mas validar antes dá erro melhor.
 *
 * [MOLDE] Para "entidade filha" (Produto->Categoria, Pedido->Cliente):
 *   - no listar, faça o JOIN com a tabela pai e traga o nome dela;
 *   - repita a validação do id do pai no inserir/atualizar.
 */
public class PetDAO {

    /** READ — lista pets; o filtro casa tanto no nome do pet quanto no do tutor. */
    public List<Pet> listar(String filtro) {
        String sql = "SELECT p.id_pet, p.nome, p.especie, p.raca, p.data_nascimento, p.id_tutor, t.nome AS nome_tutor "
                + "FROM pet p JOIN tutor t ON t.id_tutor = p.id_tutor "
                + "WHERE p.nome LIKE ? OR t.nome LIKE ? ORDER BY p.nome";
        List<Pet> lista = new ArrayList<>();
        try (Connection cn = ConnectionFactory.get();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            String like = "%" + (filtro == null ? "" : filtro.trim()) + "%";
            ps.setString(1, like);  // filtro no nome do pet
            ps.setString(2, like);  // mesmo filtro no nome do tutor
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Pet p = new Pet();
                    p.setId(rs.getInt("id_pet"));
                    p.setNome(rs.getString("nome"));
                    p.setEspecie(rs.getString("especie"));
                    p.setRaca(rs.getString("raca"));
                    // Data pode ser nula; só converte se existir. getDate -> LocalDate.
                    if (rs.getDate("data_nascimento") != null) {
                        p.setDataNascimento(rs.getDate("data_nascimento").toLocalDate());
                    }
                    p.setIdTutor(rs.getInt("id_tutor"));       // o id (FK) para edição
                    p.setNomeTutor(rs.getString("nome_tutor")); // o nome (exibição) do JOIN
                    lista.add(p);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao listar pets: " + e.getMessage(), e);
        }
        return lista;
    }

    /** CREATE — insere um pet, mas só se ele tiver um tutor válido. */
    public void inserir(Pet p) {
        // Regra de negócio: pet sem tutor não grava. Erro claro antes do banco.
        if (p.getIdTutor() <= 0) throw new IllegalArgumentException("Pet sem tutor não grava.");
        String sql = "INSERT INTO pet (nome, especie, raca, data_nascimento, id_tutor) VALUES (?, ?, ?, ?, ?)";
        try (Connection cn = ConnectionFactory.get();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, p.getNome());
            ps.setString(2, p.getEspecie());
            ps.setString(3, p.getRaca());
            // setObject aceita o LocalDate direto; se for null, grava NULL.
            ps.setObject(4, p.getDataNascimento());
            ps.setInt(5, p.getIdTutor()); // a FK: liga o pet ao tutor
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao inserir pet: " + e.getMessage(), e);
        }
    }

    /** UPDATE — altera o pet pelo id (inclusive pode trocar de tutor). */
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

    /** DELETE — remove o pet pelo id. */
    public void excluir(int id) {
        String sql = "DELETE FROM pet WHERE id_pet=?";
        try (Connection cn = ConnectionFactory.get();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            // Se o pet tiver consultas, a FK impede a exclusão.
        } catch (Exception e) {
            throw new RuntimeException("Erro ao excluir pet: " + e.getMessage(), e);
        }
    }
}
