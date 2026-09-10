package garcias.api.shared.security.infrastructure;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("PasswordHasherImpl (Argon2) Security Unit Tests")
class PasswordHasherImplTest {

    private PasswordHasherImpl passwordHasher;

    @BeforeEach
    void setUp() {
        passwordHasher = new PasswordHasherImpl();
    }

    @Test
    @DisplayName("Should hash password using Argon2id algorithm format")
    void shouldHashPasswordWithArgon2Format() {
        String rawPassword = "securePassword123!";
        String hash = passwordHasher.hash(rawPassword);

        assertNotNull(hash);
        assertTrue(hash.startsWith("$argon2id$"), "Hash must be in Argon2id format ($argon2id$)");
    }

    @Test
    @DisplayName("Should generate unique salts producing different hashes for identical passwords")
    void shouldGenerateUniqueSaltsForSamePassword() {
        String rawPassword = "samePassword123";

        String hash1 = passwordHasher.hash(rawPassword);
        String hash2 = passwordHasher.hash(rawPassword);

        assertNotNull(hash1);
        assertNotNull(hash2);
        assertNotEquals(hash1, hash2, "Argon2 must generate distinct cryptographically random salts for each invocation");
    }

    @Test
    @DisplayName("Should match correct password against generated Argon2 hash")
    void shouldMatchCorrectPassword() {
        String rawPassword = "correctPassword456";
        String hash = passwordHasher.hash(rawPassword);

        assertTrue(passwordHasher.matches(rawPassword, hash));
    }

    @Test
    @DisplayName("Should reject incorrect password against generated Argon2 hash")
    void shouldRejectIncorrectPassword() {
        String rawPassword = "correctPassword456";
        String hash = passwordHasher.hash(rawPassword);

        assertFalse(passwordHasher.matches("wrongPassword", hash));
    }

    @Test
    @DisplayName("Should reject null or empty passwords safely without throwing unexpected exceptions")
    void shouldRejectNullOrEmptySafely() {
        String hash = passwordHasher.hash("password");

        assertFalse(passwordHasher.matches("", hash));
        assertFalse(passwordHasher.matches(null, hash));
    }
}
