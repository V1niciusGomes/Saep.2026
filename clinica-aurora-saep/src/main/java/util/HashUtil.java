package util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/** SHA-256 em hex maiúsculo — igual ao SHA2(..., 256) do MySQL. */
public final class HashUtil {

    private HashUtil() {}

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
