package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Um único ponto de conexão. Se o laboratório usar outra senha do MySQL,
 * mude só este arquivo.
 */
public final class ConnectionFactory {

    private static final String URL =
            "jdbc:mysql://localhost:3306/saep_db?useSSL=false&serverTimezone=America/Sao_Paulo&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASS = "orion";

    private ConnectionFactory() {}

    public static Connection get() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
