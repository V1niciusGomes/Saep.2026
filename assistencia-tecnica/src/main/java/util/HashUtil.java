package util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * HashUtil — transforma uma senha em texto puro num "hash" SHA-256.
 *
 * Nunca guardamos a senha original no banco; guardamos o hash (irreversível).
 * No login, geramos o hash da senha digitada e comparamos com o hash salvo.
 * Devolvemos em MAIÚSCULO para bater com o UPPER(SHA2(...)) usado no SQL.
 */
public final class HashUtil {

    private HashUtil() {}

    /** Recebe a senha pura e devolve o SHA-256 em hexadecimal MAIÚSCULO (64 caracteres). */
    public static String sha256(String texto) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(texto.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(hash.length * 2);
            for (byte b : hash) {
                sb.append(String.format("%02X", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new IllegalStateException("Falha ao gerar hash", e);
        }
    }
}
