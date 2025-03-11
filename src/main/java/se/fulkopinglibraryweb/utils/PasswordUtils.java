package se.fulkopinglibraryweb.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.regex.Pattern;

public class PasswordUtils {
    
    public static final String PASSWORD_REQUIREMENTS = 
        "Password must be at least 8 characters long and contain:\n" +
        "- At least one uppercase letter (A-Z)\n" +
        "- At least one lowercase letter (a-z)\n" +
        "- At least one number (0-9)\n" +
        "- At least one special character (!@#$%^&*)";

    private static final Pattern PASSWORD_PATTERN = 
        Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*])(?=\\S+$).{8,}$");

    // Generate a random salt
    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[32]; // Increased from 16 to 32 for better security
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    // Hash the password with SHA-256 and salt
    public static String hashPassword(String password, String salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(salt.getBytes(StandardCharsets.UTF_8));
            byte[] hashedBytes = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hashedBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to hash password", e);
        }
    }

    // Verify a password against a stored hash and salt
    public static boolean verifyPassword(String password, String storedHash, String salt) {
        String hashedPassword = hashPassword(password, salt);
        return MessageDigest.isEqual(hashedPassword.getBytes(StandardCharsets.UTF_8), 
                                   storedHash.getBytes(StandardCharsets.UTF_8));
    }
    
    // Validate password against requirements
    public static boolean isValidPassword(String password) {
        if (password == null) {
            return false;
        }
        return PASSWORD_PATTERN.matcher(password).matches();
    }

    // Convert legacy MD5 hash to new format
    public static String upgradeLegacyHash(String password, String legacyHash) {
        try {
            // Verify the legacy hash first
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashBytes = md.digest(password.getBytes(StandardCharsets.UTF_8));
            String calculatedLegacyHash = Base64.getEncoder().encodeToString(hashBytes);
            
            if (!MessageDigest.isEqual(calculatedLegacyHash.getBytes(StandardCharsets.UTF_8),
                                      legacyHash.getBytes(StandardCharsets.UTF_8))) {
                return null; // Password doesn't match legacy hash
            }
            
            // Generate new salt and hash
            String newSalt = generateSalt();
            return hashPassword(password, newSalt);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 algorithm not available", e);
        }
    }
}