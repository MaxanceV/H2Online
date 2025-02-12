package tools;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Provides functionality for hashing passwords using the SHA-256 algorithm.
 */
public class PasswordManager {

    /**
     * Hashes a password string using SHA-256 and returns the resulting hash in
     * hexadecimal format.
     *
     * @param password The password to hash. Must not be null or empty.
     * @return The SHA-256 hash of the given password, in hexadecimal format.
     * @throws IllegalArgumentException If the provided password is null.
     * @throws RuntimeException If the SHA-256 algorithm is not available on the system.
     */
    public static String hashPassword(String password) {
        if (password == null) {
            throw new IllegalArgumentException("Password cannot be null");
        }

        password = password.trim();

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erreur lors du hachage du mot de passe", e);
        }
    }
}
