package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import model.Usuario;
import util.ConnectionFactory;
import util.HashUtil;

/**
 * UsuarioDAO — DAO (Data Access Object) de autenticação.
 *
 * PAPEL NA ARQUITETURA (camada dao):
 *   O DAO é a ÚNICA camada que conversa com o banco. Ele esconde o SQL do
 *   resto do sistema: a tela chama dao.autenticar(...) e recebe um objeto
 *   Usuario, sem saber nada de SELECT, conexão, etc.
 *
 * Este DAO é especial (só faz login). Os DAOs de cadastro
 * (Tutor, Pet, Veterinario, Consulta) seguem outro molde, com CRUD completo.
 */
public class UsuarioDAO {

    /**
     * Confere login + senha. Devolve o Usuario se bateu, ou null se não bateu.
     */
    public Usuario autenticar(String login, String senhaPura) {
        // O "?" é um espaço reservado (placeholder). Nunca colamos o valor
        // digitado direto na string: isso evita SQL Injection.
        String sql = "SELECT id_usuario, login, nome FROM usuario WHERE login = ? AND senha = ?";

        // Transforma a senha digitada no MESMO hash que está gravado no banco.
        String hash = HashUtil.sha256(senhaPura);

        // try-with-resources: tudo que é aberto nos parênteses (conexão e
        // statement) é fechado automaticamente no fim do bloco. Sem vazamento.
        try (Connection cn = ConnectionFactory.get();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            // Preenche os "?" na ordem: 1 = login, 2 = hash da senha.
            ps.setString(1, login);
            ps.setString(2, hash);

            // executeQuery() = consulta que RETORNA linhas (SELECT).
            try (ResultSet rs = ps.executeQuery()) {
                // Se não veio nenhuma linha, login/senha estão errados.
                if (!rs.next()) {
                    return null;
                }
                // Veio uma linha: montamos o objeto Usuario com os dados dela.
                Usuario u = new Usuario();
                u.setId(rs.getInt("id_usuario"));
                u.setLogin(rs.getString("login"));
                u.setNome(rs.getString("nome"));
                return u;
            }
        } catch (Exception e) {
            // Qualquer erro de banco vira um RuntimeException com mensagem clara,
            // que a tela captura e mostra ao usuário.
            throw new RuntimeException("Erro ao autenticar: " + e.getMessage(), e);
        }
    }
}
