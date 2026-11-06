package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * ConnectionFactory — fábrica de conexões com o banco de dados.
 *
 * É o ÚNICO ponto do sistema que sabe COMO abrir uma conexão com o MySQL.
 * Todos os DAOs chamam ConnectionFactory.get(). Para mudar de ambiente,
 * altere SÓ as 3 constantes abaixo (URL, USER, PASS).
 */
public final class ConnectionFactory {

    // Endereço do banco: jdbc:mysql://SERVIDOR:PORTA/NOME_DO_BANCO?opcoes
    private static final String URL =
            "jdbc:mysql://localhost:3306/assistencia_db?useSSL=false&serverTimezone=America/Sao_Paulo&allowPublicKeyRetrieval=true";

    private static final String USER = "root";
    private static final String PASS = "root";   // troque pela senha do seu MySQL

    private ConnectionFactory() {}

    /** Abre e devolve UMA nova conexão. Quem chama fecha (os DAOs usam try-with-resources). */
    public static Connection get() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
