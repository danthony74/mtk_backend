package com.mindthekid.utils;

/**
 * EncryptionManager Usage Examples
 * 
 * This class provides practical examples of how to use the EncryptionManager utility
 * for secure key hashing and verification in the MTK Backend system.
 * 
 * @author MTK Backend Team
 * @version 1.0.0
 */
public class EncryptionManagerExample {
    
    /**
     * Example: Basic hash generation and verification
     */
    public static void basicHashExample() {
        System.out.println("=== Basic Hash Generation and Verification ===");
        
        // Generate a hash from a user input key
        String userKey = "mySecretKey123";
        String hash = EncryptionManager.generateHash(userKey);
        
        System.out.println("User Key: " + userKey);
        System.out.println("Generated Hash: " + hash);
        
        // Verify the hash
        boolean isValid = EncryptionManager.verifyHash(userKey, hash);
        System.out.println("Hash Verification: " + (isValid ? "SUCCESS" : "FAILED"));
        
        // Try with wrong key
        boolean isInvalid = EncryptionManager.verifyHash("wrongKey", hash);
        System.out.println("Wrong Key Verification: " + (isInvalid ? "SUCCESS" : "FAILED"));
        System.out.println();
    }
    
    /**
     * Example: Input validation
     */
    public static void inputValidationExample() {
        System.out.println("=== Input Validation Examples ===");
        
        // Valid inputs
        String[] validInputs = {
            "12345678",           // Exactly minimum length
            "mySecretKey123",     // Normal key
            "my@secret#key$123!", // With special characters
            "my secret key 123",  // With spaces
            "  12345678  "        // With leading/trailing spaces
        };
        
        for (String input : validInputs) {
            try {
                String hash = EncryptionManager.generateHash(input);
                System.out.println("✓ Valid input: '" + input + "' -> Hash generated");
            } catch (IllegalArgumentException e) {
                System.out.println("✗ Invalid input: '" + input + "' -> " + e.getMessage());
            }
        }
        
        // Invalid inputs
        String[] invalidInputs = {
            null,                 // Null input
            "",                   // Empty string
            "   ",                // Only whitespace
            "1234567",            // Too short (7 characters)
            "myKey\u0000withNull" // Contains null character
        };
        
        for (String input : invalidInputs) {
            try {
                String hash = EncryptionManager.generateHash(input);
                System.out.println("✓ Unexpected success: '" + input + "' -> Hash generated");
            } catch (IllegalArgumentException e) {
                System.out.println("✗ Expected failure: '" + input + "' -> " + e.getMessage());
            }
        }
        System.out.println();
    }
    
    /**
     * Example: Secure key generation
     */
    public static void secureKeyGenerationExample() {
        System.out.println("=== Secure Key Generation ===");
        
        // Generate keys of different lengths
        int[] lengths = {8, 16, 32, 64};
        
        for (int length : lengths) {
            try {
                String secureKey = EncryptionManager.generateSecureKey(length);
                System.out.println("Generated " + length + "-char key: " + secureKey);
            } catch (IllegalArgumentException e) {
                System.out.println("Failed to generate " + length + "-char key: " + e.getMessage());
            }
        }
        
        // Generate multiple keys to show randomness
        System.out.println("\nMultiple 16-character keys (showing randomness):");
        for (int i = 0; i < 5; i++) {
            String key = EncryptionManager.generateSecureKey(16);
            System.out.println("Key " + (i + 1) + ": " + key);
        }
        System.out.println();
    }
    
    /**
     * Example: Hash uniqueness (same input, different hashes)
     */
    public static void hashUniquenessExample() {
        System.out.println("=== Hash Uniqueness (Same Input, Different Hashes) ===");
        
        String userKey = "mySecretKey123";
        
        System.out.println("Generating multiple hashes for the same key:");
        for (int i = 0; i < 5; i++) {
            String hash = EncryptionManager.generateHash(userKey);
            System.out.println("Hash " + (i + 1) + ": " + hash);
        }
        
        System.out.println("\nAll hashes are different due to random salt generation.");
        System.out.println("All hashes can be verified with the original key.");
        System.out.println();
    }
    
    /**
     * Example: Integration with user authentication
     */
    public static void authenticationExample() {
        System.out.println("=== User Authentication Example ===");
        
        // Simulate user registration
        String userPassword = "mySecurePassword123!";
        String storedHash = EncryptionManager.generateHash(userPassword);
        
        System.out.println("User Registration:");
        System.out.println("Password: " + userPassword);
        System.out.println("Stored Hash: " + storedHash);
        
        // Simulate user login - correct password
        System.out.println("\nUser Login (Correct Password):");
        boolean loginSuccess = EncryptionManager.verifyHash(userPassword, storedHash);
        System.out.println("Login Result: " + (loginSuccess ? "SUCCESS" : "FAILED"));
        
        // Simulate user login - wrong password
        System.out.println("\nUser Login (Wrong Password):");
        boolean loginFailure = EncryptionManager.verifyHash("wrongPassword", storedHash);
        System.out.println("Login Result: " + (loginFailure ? "SUCCESS" : "FAILED"));
        
        // Simulate password change
        System.out.println("\nPassword Change:");
        String newPassword = "newSecurePassword456!";
        String newHash = EncryptionManager.generateHash(newPassword);
        System.out.println("New Password: " + newPassword);
        System.out.println("New Stored Hash: " + newHash);
        
        // Verify new password
        boolean newLoginSuccess = EncryptionManager.verifyHash(newPassword, newHash);
        System.out.println("New Password Verification: " + (newLoginSuccess ? "SUCCESS" : "FAILED"));
        System.out.println();
    }
    
    /**
     * Example: Configuration and constants
     */
    public static void configurationExample() {
        System.out.println("=== Configuration and Constants ===");
        
        System.out.println("Minimum Key Length: " + EncryptionManager.getMinKeyLength() + " characters");
        System.out.println("Hash Algorithm: " + EncryptionManager.getHashAlgorithm());
        System.out.println("Salt Length: " + EncryptionManager.getSaltLength() + " bytes");
        System.out.println();
    }
    
    /**
     * Example: Error handling
     */
    public static void errorHandlingExample() {
        System.out.println("=== Error Handling Examples ===");
        
        // Test various error conditions
        try {
            EncryptionManager.generateHash(null);
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Caught null input: " + e.getMessage());
        }
        
        try {
            EncryptionManager.generateHash("1234567"); // Too short
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Caught short input: " + e.getMessage());
        }
        
        try {
            EncryptionManager.verifyHash("validKey", "invalidHashFormat");
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Caught invalid hash format: " + e.getMessage());
        }
        
        try {
            EncryptionManager.generateSecureKey(7); // Too short
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Caught short key length: " + e.getMessage());
        }
        System.out.println();
    }
    
    /**
     * Main method to run all examples
     */
    public static void main(String[] args) {
        System.out.println("EncryptionManager Usage Examples");
        System.out.println("================================\n");
        
        basicHashExample();
        inputValidationExample();
        secureKeyGenerationExample();
        hashUniquenessExample();
        authenticationExample();
        configurationExample();
        errorHandlingExample();
        
        System.out.println("All examples completed successfully!");
    }
} 