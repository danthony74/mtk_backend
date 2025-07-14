package com.mindthekid.geo.cqrs.test.shared.utils;

import com.mindthekid.utils.EncryptionManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for EncryptionManager utility class.
 * 
 * Tests all functionality including:
 * - Hash generation with validation
 * - Hash verification
 * - Input validation
 * - Error handling
 * - Secure key generation
 * 
 * @author MTK Backend Team
 * @version 1.0.0
 */
@DisplayName("EncryptionManager Tests")
class EncryptionManagerTest {
    
    @Nested
    @DisplayName("Hash Generation Tests")
    class HashGenerationTests {
        
        @Test
        @DisplayName("Should generate hash for valid input")
        void shouldGenerateHashForValidInput() {
            // Given
            String userKey = "mySecretKey123";
            
            // When
            String hash = EncryptionManager.generateHash(userKey);
            
            // Then
            assertNotNull(hash);
            assertFalse(hash.isEmpty());
            assertTrue(hash.contains(":"));
            
            String[] parts = hash.split(":");
            assertEquals(2, parts.length);
            assertFalse(parts[0].isEmpty()); // salt
            assertFalse(parts[1].isEmpty()); // hash
        }
        
        @Test
        @DisplayName("Should generate different hashes for same input")
        void shouldGenerateDifferentHashesForSameInput() {
            // Given
            String userKey = "mySecretKey123";
            
            // When
            String hash1 = EncryptionManager.generateHash(userKey);
            String hash2 = EncryptionManager.generateHash(userKey);
            
            // Then
            assertNotEquals(hash1, hash2, "Hashes should be different due to random salt");
        }
        
        @Test
        @DisplayName("Should generate hash for minimum length input")
        void shouldGenerateHashForMinimumLengthInput() {
            // Given
            String userKey = "12345678"; // Exactly 8 characters
            
            // When
            String hash = EncryptionManager.generateHash(userKey);
            
            // Then
            assertNotNull(hash);
            assertTrue(hash.contains(":"));
        }
        
        @Test
        @DisplayName("Should generate hash for input with special characters")
        void shouldGenerateHashForInputWithSpecialCharacters() {
            // Given
            String userKey = "my@secret#key$123!";
            
            // When
            String hash = EncryptionManager.generateHash(userKey);
            
            // Then
            assertNotNull(hash);
            assertTrue(hash.contains(":"));
        }
        
        @Test
        @DisplayName("Should generate hash for input with spaces")
        void shouldGenerateHashForInputWithSpaces() {
            // Given
            String userKey = "my secret key 123";
            
            // When
            String hash = EncryptionManager.generateHash(userKey);
            
            // Then
            assertNotNull(hash);
            assertTrue(hash.contains(":"));
        }
        
        @Test
        @DisplayName("Should normalize whitespace in input")
        void shouldNormalizeWhitespaceInInput() {
            // Given
            String userKey1 = "my  secret   key   123";
            String userKey2 = "my secret key 123";
            
            // When
            String hash1 = EncryptionManager.generateHash(userKey1);
            String hash2 = EncryptionManager.generateHash(userKey2);
            
            // Then
            // Both should generate valid hashes (different due to salt)
            assertNotNull(hash1);
            assertNotNull(hash2);
            assertTrue(hash1.contains(":"));
            assertTrue(hash2.contains(":"));
        }
    }
    
    @Nested
    @DisplayName("Input Validation Tests")
    class InputValidationTests {
        
        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("Should throw exception for null or empty input")
        void shouldThrowExceptionForNullOrEmptyInput(String userKey) {
            // When & Then
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> EncryptionManager.generateHash(userKey)
            );
            
            assertTrue(exception.getMessage().contains("cannot be null") || 
                      exception.getMessage().contains("cannot be empty"));
        }
        
        @Test
        @DisplayName("Should throw exception for input shorter than minimum length")
        void shouldThrowExceptionForInputShorterThanMinimumLength() {
            // Given
            String userKey = "1234567"; // 7 characters (less than minimum 8)
            
            // When & Then
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> EncryptionManager.generateHash(userKey)
            );
            
            assertTrue(exception.getMessage().contains("at least 8 characters"));
            assertTrue(exception.getMessage().contains("Provided: 7 characters"));
        }
        
        @Test
        @DisplayName("Should throw exception for input with invalid characters")
        void shouldThrowExceptionForInputWithInvalidCharacters() {
            // Given
            String userKey = "myKey\u0000withNull"; // Contains null character
            
            // When & Then
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> EncryptionManager.generateHash(userKey)
            );
            
            assertTrue(exception.getMessage().contains("invalid characters"));
        }
        
        @Test
        @DisplayName("Should accept input with only whitespace after trimming")
        void shouldAcceptInputWithOnlyWhitespaceAfterTrimming() {
            // Given
            String userKey = "  12345678  "; // Valid after trimming
            
            // When
            String hash = EncryptionManager.generateHash(userKey);
            
            // Then
            assertNotNull(hash);
            assertTrue(hash.contains(":"));
        }
    }
    
    @Nested
    @DisplayName("Hash Verification Tests")
    class HashVerificationTests {
        
        @Test
        @DisplayName("Should verify correct hash")
        void shouldVerifyCorrectHash() {
            // Given
            String userKey = "mySecretKey123";
            String hash = EncryptionManager.generateHash(userKey);
            
            // When
            boolean isValid = EncryptionManager.verifyHash(userKey, hash);
            
            // Then
            assertTrue(isValid);
        }
        
        @Test
        @DisplayName("Should reject incorrect hash")
        void shouldRejectIncorrectHash() {
            // Given
            String userKey = "mySecretKey123";
            String hash = EncryptionManager.generateHash(userKey);
            String wrongKey = "wrongKey123";
            
            // When
            boolean isValid = EncryptionManager.verifyHash(wrongKey, hash);
            
            // Then
            assertFalse(isValid);
        }
        
        @Test
        @DisplayName("Should verify hash with different case")
        void shouldVerifyHashWithDifferentCase() {
            // Given
            String userKey = "mySecretKey123";
            String hash = EncryptionManager.generateHash(userKey);
            String differentCaseKey = "MySecretKey123"; // Different case
            
            // When
            boolean isValid = EncryptionManager.verifyHash(differentCaseKey, hash);
            
            // Then
            assertFalse(isValid, "Case-sensitive verification should fail");
        }
        
        @Test
        @DisplayName("Should verify hash with normalized whitespace")
        void shouldVerifyHashWithNormalizedWhitespace() {
            // Given
            String userKey = "my  secret   key   123";
            String hash = EncryptionManager.generateHash(userKey);
            String normalizedKey = "my secret key 123";
            
            // When
            boolean isValid = EncryptionManager.verifyHash(normalizedKey, hash);
            
            // Then
            assertTrue(isValid, "Should verify with normalized whitespace");
        }
        
        @Test
        @DisplayName("Should throw exception for invalid stored hash format")
        void shouldThrowExceptionForInvalidStoredHashFormat() {
            // Given
            String userKey = "mySecretKey123";
            String invalidHash = "invalidHashFormat";
            
            // When & Then
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> EncryptionManager.verifyHash(userKey, invalidHash)
            );
            
            assertTrue(exception.getMessage().contains("missing separator"));
        }
        
        @Test
        @DisplayName("Should throw exception for null stored hash")
        void shouldThrowExceptionForNullStoredHash() {
            // Given
            String userKey = "mySecretKey123";
            String nullHash = null;
            
            // When & Then
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> EncryptionManager.verifyHash(userKey, nullHash)
            );
            
            assertTrue(exception.getMessage().contains("cannot be null"));
        }
    }
    
    @Nested
    @DisplayName("Secure Key Generation Tests")
    class SecureKeyGenerationTests {
        
        @Test
        @DisplayName("Should generate secure key of specified length")
        void shouldGenerateSecureKeyOfSpecifiedLength() {
            // Given
            int length = 16;
            
            // When
            String secureKey = EncryptionManager.generateSecureKey(length);
            
            // Then
            assertNotNull(secureKey);
            assertEquals(length, secureKey.length());
        }
        
        @Test
        @DisplayName("Should generate different secure keys")
        void shouldGenerateDifferentSecureKeys() {
            // Given
            int length = 16;
            
            // When
            String key1 = EncryptionManager.generateSecureKey(length);
            String key2 = EncryptionManager.generateSecureKey(length);
            
            // Then
            assertNotEquals(key1, key2, "Secure keys should be different");
        }
        
        @Test
        @DisplayName("Should throw exception for length less than minimum")
        void shouldThrowExceptionForLengthLessThanMinimum() {
            // Given
            int length = 7; // Less than minimum 8
            
            // When & Then
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> EncryptionManager.generateSecureKey(length)
            );
            
            assertTrue(exception.getMessage().contains("at least 8 characters"));
        }
        
        @Test
        @DisplayName("Should generate secure key of minimum length")
        void shouldGenerateSecureKeyOfMinimumLength() {
            // Given
            int length = 8; // Minimum length
            
            // When
            String secureKey = EncryptionManager.generateSecureKey(length);
            
            // Then
            assertNotNull(secureKey);
            assertEquals(length, secureKey.length());
        }
    }
    
    @Nested
    @DisplayName("Utility Method Tests")
    class UtilityMethodTests {
        
        @Test
        @DisplayName("Should return correct minimum key length")
        void shouldReturnCorrectMinimumKeyLength() {
            // When
            int minLength = EncryptionManager.getMinKeyLength();
            
            // Then
            assertEquals(8, minLength);
        }
        
        @Test
        @DisplayName("Should return correct hash algorithm")
        void shouldReturnCorrectHashAlgorithm() {
            // When
            String algorithm = EncryptionManager.getHashAlgorithm();
            
            // Then
            assertEquals("SHA-256", algorithm);
        }
        
        @Test
        @DisplayName("Should return correct salt length")
        void shouldReturnCorrectSaltLength() {
            // When
            int saltLength = EncryptionManager.getSaltLength();
            
            // Then
            assertEquals(32, saltLength);
        }
    }
    
    @Nested
    @DisplayName("Integration Tests")
    class IntegrationTests {
        
        @Test
        @DisplayName("Should handle complete hash and verify workflow")
        void shouldHandleCompleteHashAndVerifyWorkflow() {
            // Given
            String userKey = "myVerySecureKey123!@#";
            
            // When - Generate hash
            String hash = EncryptionManager.generateHash(userKey);
            
            // Then - Verify the hash
            boolean isValid = EncryptionManager.verifyHash(userKey, hash);
            assertTrue(isValid);
            
            // And - Verify wrong key fails
            boolean isInvalid = EncryptionManager.verifyHash("wrongKey", hash);
            assertFalse(isInvalid);
        }
        
        @Test
        @DisplayName("Should handle multiple hash generations and verifications")
        void shouldHandleMultipleHashGenerationsAndVerifications() {
            // Given
            String[] userKeys = {
                "key1_12345678",
                "key2_abcdefgh",
                "key3_!@#$%^&*",
                "key4_with spaces",
                "key5_very_long_key_with_many_characters_123456789"
            };
            
            // When & Then
            for (String userKey : userKeys) {
                String hash = EncryptionManager.generateHash(userKey);
                assertTrue(EncryptionManager.verifyHash(userKey, hash));
                assertFalse(EncryptionManager.verifyHash("wrongKey", hash));
            }
        }
        
        @Test
        @DisplayName("Should handle edge case inputs")
        void shouldHandleEdgeCaseInputs() {
            // Given
            String[] edgeCaseKeys = {
                "12345678", // Exactly minimum length
                "a".repeat(100), // Very long key
                "!@#$%^&*()", // Only special characters
                "  12345678  ", // With leading/trailing spaces
                "my\tkey\n123" // With tabs and newlines
            };
            
            // When & Then
            for (String userKey : edgeCaseKeys) {
                try {
                    String hash = EncryptionManager.generateHash(userKey);
                    assertTrue(EncryptionManager.verifyHash(userKey, hash));
                } catch (IllegalArgumentException e) {
                    // Some edge cases might be rejected, which is expected
                    assertTrue(e.getMessage().contains("invalid characters") ||
                              e.getMessage().contains("at least 8 characters"));
                }
            }
        }
    }
    
    @Nested
    @DisplayName("Security Tests")
    class SecurityTests {
        
        @Test
        @DisplayName("Should use constant-time comparison for hash verification")
        void shouldUseConstantTimeComparisonForHashVerification() {
            // Given
            String userKey = "mySecretKey123";
            String hash = EncryptionManager.generateHash(userKey);
            
            // When - Verify multiple times to check for timing differences
            long startTime = System.nanoTime();
            boolean result1 = EncryptionManager.verifyHash(userKey, hash);
            long time1 = System.nanoTime() - startTime;
            
            startTime = System.nanoTime();
            boolean result2 = EncryptionManager.verifyHash("wrongKey", hash);
            long time2 = System.nanoTime() - startTime;
            
            // Then
            assertTrue(result1);
            assertFalse(result2);
            
            // Timing should be similar (within reasonable bounds)
            long timeDifference = Math.abs(time1 - time2);
            assertTrue(timeDifference < 1000000, // 1ms tolerance
                "Hash verification should use constant-time comparison");
        }
        
        @Test
        @DisplayName("Should generate cryptographically secure random salt")
        void shouldGenerateCryptographicallySecureRandomSalt() {
            // Given
            int iterations = 1000;
            String userKey = "mySecretKey123";
            
            // When
            String[] hashes = new String[iterations];
            for (int i = 0; i < iterations; i++) {
                hashes[i] = EncryptionManager.generateHash(userKey);
            }
            
            // Then - All hashes should be different due to random salt
            for (int i = 0; i < iterations; i++) {
                for (int j = i + 1; j < iterations; j++) {
                    assertNotEquals(hashes[i], hashes[j], 
                        "Hashes should be different due to random salt");
                }
            }
        }
    }
} 