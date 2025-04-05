package org.example.lamebeats.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordHashUtil {

    private static final int SALT_LENGTH = 16; // 128 bits
    private static final String HASH_ALGORITHM = "SHA-256";
    private static final String SALT_PASSWORD_SEPARATOR = ":";

    /**
     * Hashes a password using SHA-256 and a random salt
     * @param password The plain text password
     * @return A string containing the Base64 encoded salt and hash, separated by ":"
     */
    public static String hashPassword(String password) {
        try {
            // Generate random salt
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[SALT_LENGTH];
            random.nextBytes(salt);

            // Hash the password with the salt
            MessageDigest md = MessageDigest.getInstance(HASH_ALGORITHM);
            md.update(salt);
            byte[] hashedPassword = md.digest(password.getBytes());

            // Encode both salt and hash with Base64
            String encodedSalt = Base64.getEncoder().encodeToString(salt);
            String encodedHash = Base64.getEncoder().encodeToString(hashedPassword);

            // Return salt and hash combined
            return encodedSalt + SALT_PASSWORD_SEPARATOR + encodedHash;

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    /**
     * Verifies a password against a stored hash
     * @param password The plain text password to verify
     * @param storedHash The previously stored hash (salt:hash)
     * @return true if the password matches, false otherwise
     */
    public static boolean verifyPassword(String password, String storedHash) {
        try {
            // Split the stored value to get salt and hash
            String[] parts = storedHash.split(SALT_PASSWORD_SEPARATOR);
            if (parts.length != 2) {
                return false; // Invalid stored hash format
            }

            // Decode the stored salt and hash
            byte[] salt = Base64.getDecoder().decode(parts[0]);
            byte[] originalHash = Base64.getDecoder().decode(parts[1]);

            // Hash the input password with the same salt
            MessageDigest md = MessageDigest.getInstance(HASH_ALGORITHM);
            md.update(salt);
            byte[] newHash = md.digest(password.getBytes());

            // Compare the two hashes
            if (originalHash.length != newHash.length) {
                return false;
            }

            // Time-constant comparison to prevent timing attacks
            int diff = 0;
            for (int i = 0; i < originalHash.length; i++) {
                diff |= originalHash[i] ^ newHash[i];
            }

            return diff == 0;

        } catch (NoSuchAlgorithmException | IllegalArgumentException e) {
            return false; // If anything goes wrong, authentication fails
        }
    }
}