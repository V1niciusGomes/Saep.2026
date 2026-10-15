package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import model.Usuario;
import util.ConnectionFactory;
import util.HashUtil;

public class UsuarioDAO {

    public Usuario autenticar(String login, String senhaPura) {
        String sql = "SELECT id_usuario, login, nome FROM usuario WHERE login = ? AND senha = ?";
        String hash = HashUtil.sha256(senhaPura);
        try (Connection cn = ConnectionFactory.get();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, login);
            ps.setString(2, hash);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                Usuario u = new Usuario();
                u.setId(rs.getInt("id_usuario"));
                u.setLogin(rs.getString("login"));
                u.setNome(rs.getString("nome"));
                return u;
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao autenticar: " + e.getMessage(), e);
        }
    }
}
