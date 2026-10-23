package util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * HashUtil — transforma uma senha em texto puro num "hash" SHA-256.
 *
 * PAPEL NA ARQUITETURA (camada util):
 *   Segurança de senha. Nunca guardamos a senha original no banco; guardamos
 *   o hash (uma "impressão digital" irreversível). No login, geramos o hash
 *   da senha digitada e comparamos com o hash salvo.
 *
 * POR QUE MAIÚSCULO:
 *   O MySQL tem a função SHA2('texto', 256) que gera o mesmo hash, mas em
 *   letras minúsculas. Aqui devolvemos em MAIÚSCULO para bater exatamente com
 *   o que o script SQL grava (lá usamos UPPER(SHA2(...))). Os dois lados
 *   precisam gerar a MESMA string, senão o login nunca confere.
 *
 * [MOLDE] Esta classe é genérica: serve para qualquer projeto com login.
 *         Normalmente não precisa mexer em nada aqui.
 */
public final class HashUtil {

    private HashUtil() {} // classe utilitária: sem instâncias

    /** Recebe a senha pura e devolve o SHA-256 em hexadecimal MAIÚSCULO (64 caracteres). */
    public static String sha256(String texto) {
        try {
            // MessageDigest é a "máquina" de hash do próprio Java.
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            // Converte o texto em bytes (UTF-8) e calcula o hash desses bytes.
            byte[] hash = md.digest(texto.getBytes(StandardCharsets.UTF_8));

            // O hash vem como bytes; aqui montamos a string lendo byte a byte
            // e escrevendo cada um como 2 dígitos hexadecimais maiúsculos (%02X).
            StringBuilder sb = new StringBuilder(hash.length * 2);
            for (byte b : hash) {
                sb.append(String.format("%02X", b));
            }
            return sb.toString();
        } catch (Exception e) {
            // Se a JVM não tiver o algoritmo (praticamente impossível), falha claro.
            throw new IllegalStateException("Falha ao gerar hash", e);
        }
    }
}
