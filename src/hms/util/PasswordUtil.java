package hms.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Utility class for password hashing and verification.
 * Uses SHA-256 with salt for secure password storage.
 * 
 * NOTE: In production, use BCrypt library (org.mindrot:jbcrypt)
 * This is a simplified implementation for educational purposes.
 * 
 * @author HMS Team
 * @version 1.0
 */
public class PasswordUtil {

    private static final String ALGORITHM = "SHA-256";
    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * Hashes a password with a generated salt using SHA-256.
     * Returns: base64(salt) + ":" + base64(hash)
     * 
     * @param plainPassword the plain text password
     * @return salted hash as string
     */
    public static String hashPassword(String plainPassword) {
        try {
            // Generate a random salt
            byte[] salt = new byte[16];
            RANDOM.nextBytes(salt);

            // Hash the password with salt
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            md.update(salt);
            byte[] hashedPassword = md.digest(plainPassword.getBytes());

            // Combine salt and hash
            String saltBase64 = Base64.getEncoder().encodeToString(salt);
            String hashBase64 = Base64.getEncoder().encodeToString(hashedPassword);

            return saltBase64 + ":" + hashBase64;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    /**
     * Verifies a plain password against its stored hash.
     * 
     * @param plainPassword the plain text password to verify
     * @param storedHash the stored hash (from database)
     * @return true if passwords match, false otherwise
     */
    public static boolean verifyPassword(String plainPassword, String storedHash) {
        try {
            // Extract salt and hash from stored value
            String[] parts = storedHash.split(":");
            if (parts.length != 2) {
                return false;
            }

            byte[] salt = Base64.getDecoder().decode(parts[0]);
            String storedHashBase64 = parts[1];

            // Hash the provided password with the same salt
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            md.update(salt);
            byte[] hashedPassword = md.digest(plainPassword.getBytes());
            String computedHashBase64 = Base64.getEncoder().encodeToString(hashedPassword);

            // Compare the hashes
            return storedHashBase64.equals(computedHashBase64);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Generates a random temporary password for password reset functionality.
     * 
     * @param length length of the temporary password
     * @return random password string
     */
    public static String generateTemporaryPassword(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^";
        StringBuilder password = new StringBuilder();

        for (int i = 0; i < length; i++) {
            password.append(characters.charAt(RANDOM.nextInt(characters.length())));
        }

        return password.toString();
    }
}
