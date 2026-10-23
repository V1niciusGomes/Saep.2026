package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * ConnectionFactory — fábrica de conexões com o banco.
 *
 * PAPEL NA ARQUITETURA (camada util):
 *   É o único ponto do sistema que sabe COMO abrir uma conexão com o MySQL.
 *   Todos os DAOs chamam ConnectionFactory.get() em vez de montar a conexão
 *   na mão. Assim, se o banco mudar (senha, servidor, porta), você altera
 *   apenas AQUI e o resto do sistema continua igual.
 *
 * [MOLDE] Para adaptar a outro ambiente/projeto, troque só as 3 constantes
 *         abaixo (URL, USER, PASS). O nome do banco ("saep_db") está dentro
 *         da URL — mude ali se o seu banco tiver outro nome.
 */
public final class ConnectionFactory {

    // [MOLDE] Endereço do banco. Formato:
    //   jdbc:mysql://SERVIDOR:PORTA/NOME_DO_BANCO?opcoes
    // - localhost:3306 -> servidor e porta padrão do MySQL na própria máquina
    // - saep_db        -> nome do banco (troque pelo seu)
    // - useSSL=false / allowPublicKeyRetrieval=true -> evitam travas comuns em laboratório
    // - serverTimezone -> fuso, para datas/horas não "pularem" de horário
    private static final String URL =
            "jdbc:mysql://localhost:3306/saep_db?useSSL=false&serverTimezone=America/Sao_Paulo&allowPublicKeyRetrieval=true";

    // [MOLDE] Usuário do MySQL (geralmente "root" em ambiente de estudo).
    private static final String USER = "root";

    // [MOLDE] Senha do MySQL. Mude para a senha da sua máquina.
    private static final String PASS = "senai";

    // Construtor privado: ninguém cria "new ConnectionFactory()".
    // A classe é só um porta-chaves de métodos estáticos.
    private ConnectionFactory() {}

    /**
     * Abre e devolve UMA nova conexão com o banco.
     * Quem chama é responsável por fechar (os DAOs fazem isso com
     * try-with-resources, que fecha sozinho no fim do bloco).
     */
    public static Connection get() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
