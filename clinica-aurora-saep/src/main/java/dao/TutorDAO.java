package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import model.Tutor;
import util.ConnectionFactory;

/**
 * TutorDAO — EXEMPLO-MESTRE de um DAO com CRUD completo.
 *
 * CRUD = as 4 operações básicas de qualquer cadastro:
 *   - listar   (READ)   -> SELECT
 *   - inserir  (CREATE) -> INSERT
 *   - atualizar(UPDATE) -> UPDATE
 *   - excluir  (DELETE) -> DELETE
 *
 * PADRÃO repetido em TODOS os métodos (decore este esqueleto):
 *   1. monta o SQL com "?" nos valores;
 *   2. abre conexão + statement no try-with-resources (fecha sozinho);
 *   3. preenche os "?" com ps.setXxx(posição, valor);
 *   4. executa (executeQuery para SELECT, executeUpdate para gravação);
 *   5. em erro, lança RuntimeException com mensagem clara.
 *
 * EXTRA deste DAO: o CPF é criptografado com AES dentro do próprio SQL
 *   (AES_ENCRYPT na gravação, AES_DECRYPT na leitura), usando a chave da sala.
 *
 * [MOLDE] Para um cadastro simples novo, copie este DAO inteiro e:
 *   - troque Tutor -> SuaEntidade e tutor/id_tutor -> sua_tabela/sua_pk;
 *   - ajuste a lista de colunas nos SELECT/INSERT/UPDATE;
 *   - se não precisar de AES, use os campos direto (veja VeterinarioDAO).
 */
public class TutorDAO {

    // [MOLDE] Chave do AES, fixa da sala. Em produção isso NÃO fica no código.
    private static final String KEY = "SENAI2026";

    /** READ — lista tutores cujo nome contém o filtro (ou todos, se filtro vazio). */
    public List<Tutor> listar(String filtro) {
        // CAST(AES_DECRYPT(cpf, ?) AS CHAR): descriptografa o CPF já na consulta,
        // então a tela recebe o CPF em texto puro.
        String sql = "SELECT id_tutor, nome, telefone, email, "
                + "CAST(AES_DECRYPT(cpf, ?) AS CHAR) AS cpf "
                + "FROM tutor WHERE nome LIKE ? ORDER BY nome";

        List<Tutor> lista = new ArrayList<>();
        try (Connection cn = ConnectionFactory.get();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            // LIKE com % dos dois lados = "contém". filtro null vira "" (lista tudo).
            String like = "%" + (filtro == null ? "" : filtro.trim()) + "%";
            ps.setString(1, KEY);   // 1º ? -> chave do AES_DECRYPT
            ps.setString(2, like);  // 2º ? -> filtro do LIKE

            try (ResultSet rs = ps.executeQuery()) {
                // Para cada linha retornada, cria um Tutor e adiciona na lista.
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

    /** CREATE — insere um novo tutor. O CPF entra criptografado (AES_ENCRYPT). */
    public void inserir(Tutor t) {
        String sql = "INSERT INTO tutor (nome, telefone, email, cpf) VALUES (?, ?, ?, AES_ENCRYPT(?, ?))";
        try (Connection cn = ConnectionFactory.get();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, t.getNome());
            ps.setString(2, t.getTelefone());
            ps.setString(3, t.getEmail());
            ps.setString(4, t.getCpf() == null ? "" : t.getCpf()); // valor a criptografar
            ps.setString(5, KEY);                                   // chave do AES
            // executeUpdate() = comando que ALTERA o banco (INSERT/UPDATE/DELETE).
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao inserir tutor: " + e.getMessage(), e);
        }
    }

    /** UPDATE — altera o tutor cujo id_tutor casa com t.getId(). */
    public void atualizar(Tutor t) {
        String sql = "UPDATE tutor SET nome=?, telefone=?, email=?, cpf=AES_ENCRYPT(?, ?) WHERE id_tutor=?";
        try (Connection cn = ConnectionFactory.get();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, t.getNome());
            ps.setString(2, t.getTelefone());
            ps.setString(3, t.getEmail());
            ps.setString(4, t.getCpf() == null ? "" : t.getCpf());
            ps.setString(5, KEY);
            ps.setInt(6, t.getId());  // o WHERE: diz QUAL linha atualizar
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao atualizar tutor: " + e.getMessage(), e);
        }
    }

    /** DELETE — remove o tutor pelo id. Sem AES (não precisa ler o CPF para apagar). */
    public void excluir(int id) {
        String sql = "DELETE FROM tutor WHERE id_tutor=?";
        try (Connection cn = ConnectionFactory.get();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            // Obs.: se o tutor tiver pets, o banco recusa a exclusão por causa da FK.
        } catch (Exception e) {
            throw new RuntimeException("Erro ao excluir tutor: " + e.getMessage(), e);
        }
    }
}
