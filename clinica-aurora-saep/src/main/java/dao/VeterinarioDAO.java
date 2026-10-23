package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import model.Veterinario;
import util.ConnectionFactory;

/**
 * VeterinarioDAO — CRUD simples, SEM criptografia e SEM relacionamento.
 * É a versão mais "limpa" do molde de DAO: bom ponto de partida para copiar.
 *
 * Mesmo esqueleto do TutorDAO (listar/inserir/atualizar/excluir), só que
 * sem AES. A única sutileza fica no banco: a coluna "crmv" é UNIQUE, então
 * se você tentar gravar um CRMV repetido o MySQL recusa e cai no catch.
 *
 * [MOLDE] Copie esta classe para qualquer cadastro independente:
 *   troque Veterinario -> SuaEntidade, veterinario/id_veterinario pela sua
 *   tabela/pk, e ajuste as colunas (nome, crmv, especialidade).
 */
public class VeterinarioDAO {

    /** READ — lista por nome (filtro vazio = todos), em ordem alfabética. */
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

    /** CREATE — insere um veterinário. */
    public void inserir(Veterinario v) {
        String sql = "INSERT INTO veterinario (nome, crmv, especialidade) VALUES (?, ?, ?)";
        try (Connection cn = ConnectionFactory.get();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, v.getNome());
            ps.setString(2, v.getCrmv());
            ps.setString(3, v.getEspecialidade());
            ps.executeUpdate();
        } catch (Exception e) {
            // Cai aqui também quando o CRMV já existe (violação de UNIQUE).
            throw new RuntimeException("Erro ao inserir veterinário: " + e.getMessage(), e);
        }
    }

    /** UPDATE — altera o veterinário pelo id. */
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

    /** DELETE — remove pelo id. */
    public void excluir(int id) {
        String sql = "DELETE FROM veterinario WHERE id_veterinario=?";
        try (Connection cn = ConnectionFactory.get();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            // Se houver consultas desse veterinário, a FK impede a exclusão.
        } catch (Exception e) {
            throw new RuntimeException("Erro ao excluir veterinário: " + e.getMessage(), e);
        }
    }
}
