package com.mindthekid.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.regex.Pattern;

/**
 * Encryption Manager Utility Class
 * 
 * Provides robust encryption and hashing utilities for the MTK Backend system.
 * This class contains static methods for secure key generation, hashing, and validation.
 * 
 * Features:
 * - SHA-256 hashing with salt
 * - Minimum 8 character input validation
 * - Secure random salt generation
 * - Base64 encoding for safe storage
 * - Input validation and sanitization
 * 
 * @author MTK Backend Team
 * @version 1.0.0
 */
public final class EncryptionManager {
    
    // Constants
    private static final int MIN_KEY_LENGTH = 8;
    private static final int SALT_LENGTH = 32;
    private static final String HASH_ALGORITHM = "SHA-256";
    private static final String SEPARATOR = ":";
    
    // Input validation patterns
    private static final Pattern VALID_INPUT_PATTERN = Pattern.compile("^[\\w\\d\\s!@#$%^&*()_+\\-=\\[\\]{}|;':\",./<>?`~]+$");
    private static final Pattern WHITESPACE_PATTERN = Pattern.compile("\\s+");
    
    // Secure random instance for salt generation
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    
    /**
     * Private constructor to prevent instantiation
     */
    private EncryptionManager() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }
    
    /**
     * Generates a secure hash from a user input key.
     * 
     * This method performs the following operations:
     * 1. Validates input length (minimum 8 characters)
     * 2. Sanitizes and normalizes the input
     * 3. Generates a cryptographically secure random salt
     * 4. Combines the input with the salt
     * 5. Applies SHA-256 hashing
     * 6. Returns the salt and hash combined for storage
     * 
     * @param userKey The user input key to hash (minimum 8 characters)
     * @return A string containing the salt and hash separated by ":"
     * @throws IllegalArgumentException if the input is null, empty, or less than 8 characters
     * @throws RuntimeException if there's an error during hashing
     * 
     * @example
     * String hash = EncryptionManager.generateHash("mySecretKey123");
     * // Returns: "base64Salt:base64Hash"
     */
    public static String generateHash(String userKey) {
        // Input validation
        validateInput(userKey);
        
        try {
            // Sanitize and normalize input
            String sanitizedKey = sanitizeInput(userKey);
            
            // Generate secure random salt
            byte[] salt = generateSalt();
            
            // Combine salt and key
            byte[] saltedKey = combineSaltAndKey(salt, sanitizedKey);
            
            // Generate hash
            byte[] hash = generateHashBytes(saltedKey);
            
            // Encode salt and hash for storage
            String encodedSalt = Base64.getEncoder().encodeToString(salt);
            String encodedHash = Base64.getEncoder().encodeToString(hash);
            
            // Return salt:hash format
            return encodedSalt + SEPARATOR + encodedHash;
            
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Hash algorithm not available: " + HASH_ALGORITHM, e);
        } catch (Exception e) {
            throw new RuntimeException("Error generating hash", e);
        }
    }
    
    /**
     * Verifies a user input key against a stored hash.
     * 
     * @param userKey The user input key to verify
     * @param storedHash The stored hash in format "salt:hash"
     * @return true if the key matches the hash, false otherwise
     * @throws IllegalArgumentException if inputs are invalid
     */
    public static boolean verifyHash(String userKey, String storedHash) {
        // Input validation
        validateInput(userKey);
        validateStoredHash(storedHash);
        
        try {
            // Parse stored hash
            String[] parts = storedHash.split(SEPARATOR);
            if (parts.length != 2) {
                throw new IllegalArgumentException("Invalid stored hash format");
            }
            
            String encodedSalt = parts[0];
            String encodedHash = parts[1];
            
            // Decode salt and hash
            byte[] salt = Base64.getDecoder().decode(encodedSalt);
            byte[] expectedHash = Base64.getDecoder().decode(encodedHash);
            
            // Sanitize input key
            String sanitizedKey = sanitizeInput(userKey);
            
            // Combine salt and key
            byte[] saltedKey = combineSaltAndKey(salt, sanitizedKey);
            
            // Generate hash for comparison
            byte[] actualHash = generateHashBytes(saltedKey);
            
            // Compare hashes (constant-time comparison to prevent timing attacks)
            return MessageDigest.isEqual(expectedHash, actualHash);
            
        } catch (Exception e) {
            throw new RuntimeException("Error verifying hash", e);
        }
    }
    
    /**
     * Generates a secure random salt.
     * 
     * @return A byte array containing the salt
     */
    private static byte[] generateSalt() {
        byte[] salt = new byte[SALT_LENGTH];
        SECURE_RANDOM.nextBytes(salt);
        return salt;
    }
    
    /**
     * Combines salt and key for hashing.
     * 
     * @param salt The salt bytes
     * @param key The sanitized key
     * @return Combined byte array
     */
    private static byte[] combineSaltAndKey(byte[] salt, String key) {
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        byte[] combined = new byte[salt.length + keyBytes.length];
        
        System.arraycopy(salt, 0, combined, 0, salt.length);
        System.arraycopy(keyBytes, 0, combined, salt.length, keyBytes.length);
        
        return combined;
    }
    
    /**
     * Generates hash bytes using SHA-256.
     * 
     * @param input The input bytes to hash
     * @return The hash bytes
     * @throws NoSuchAlgorithmException if SHA-256 is not available
     */
    private static byte[] generateHashBytes(byte[] input) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
        return digest.digest(input);
    }
    
    /**
     * Validates user input key.
     * 
     * @param userKey The key to validate
     * @throws IllegalArgumentException if validation fails
     */
    private static void validateInput(String userKey) {
        if (userKey == null) {
            throw new IllegalArgumentException("User key cannot be null");
        }
        
        if (userKey.trim().isEmpty()) {
            throw new IllegalArgumentException("User key cannot be empty");
        }
        
        if (userKey.length() < MIN_KEY_LENGTH) {
            throw new IllegalArgumentException(
                String.format("User key must be at least %d characters long. Provided: %d characters", 
                    MIN_KEY_LENGTH, userKey.length())
            );
        }
        
        if (!VALID_INPUT_PATTERN.matcher(userKey).matches()) {
            throw new IllegalArgumentException("User key contains invalid characters");
        }
    }
    
    /**
     * Validates stored hash format.
     * 
     * @param storedHash The hash to validate
     * @throws IllegalArgumentException if validation fails
     */
    private static void validateStoredHash(String storedHash) {
        if (storedHash == null) {
            throw new IllegalArgumentException("Stored hash cannot be null");
        }
        
        if (storedHash.trim().isEmpty()) {
            throw new IllegalArgumentException("Stored hash cannot be empty");
        }
        
        if (!storedHash.contains(SEPARATOR)) {
            throw new IllegalArgumentException("Invalid stored hash format: missing separator");
        }
    }
    
    /**
     * Sanitizes and normalizes user input.
     * 
     * @param input The input to sanitize
     * @return The sanitized input
     */
    private static String sanitizeInput(String input) {
        // Trim whitespace
        String trimmed = input.trim();
        
        // Normalize whitespace (replace multiple spaces with single space)
        String normalized = WHITESPACE_PATTERN.matcher(trimmed).replaceAll(" ");
        
        return normalized;
    }
    
    /**
     * Generates a secure random key of specified length.
     * 
     * @param length The length of the key to generate
     * @return A secure random key
     * @throws IllegalArgumentException if length is less than minimum
     */
    public static String generateSecureKey(int length) {
        if (length < MIN_KEY_LENGTH) {
            throw new IllegalArgumentException(
                String.format("Key length must be at least %d characters", MIN_KEY_LENGTH)
            );
        }
        
        byte[] keyBytes = new byte[length];
        SECURE_RANDOM.nextBytes(keyBytes);
        
        // Convert to base64 and truncate to desired length
        String base64Key = Base64.getEncoder().encodeToString(keyBytes);
        return base64Key.substring(0, Math.min(length, base64Key.length()));
    }
    
    /**
     * Gets the minimum key length required.
     * 
     * @return The minimum key length
     */
    public static int getMinKeyLength() {
        return MIN_KEY_LENGTH;
    }
    
    /**
     * Gets the hash algorithm used.
     * 
     * @return The hash algorithm name
     */
    public static String getHashAlgorithm() {
        return HASH_ALGORITHM;
    }
    
    /**
     * Gets the salt length used.
     * 
     * @return The salt length in bytes
     */
    public static int getSaltLength() {
        return SALT_LENGTH;
    }
} 