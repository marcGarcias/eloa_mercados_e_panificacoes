package garcias.api.identity.user.domain.valueobjects;

import garcias.api.shared.exceptions.AttributeCannotBeEmptyException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Password Value Object Security Unit Tests")
class PasswordValueObjectTest {

    @Test
    @DisplayName("Should create Password instance with valid value")
    void shouldCreatePasswordWithValidValue() {
        Password password = new Password("Secr3t!P@ss");

        assertNotNull(password);
        assertEquals("Secr3t!P@ss", password.value());
    }

    @Test
    @DisplayName("Should throw AttributeCannotBeEmptyException when password is null")
    void shouldThrowWhenPasswordIsNull() {
        AttributeCannotBeEmptyException ex = assertThrows(AttributeCannotBeEmptyException.class, () -> new Password(null));
        assertTrue(ex.getMessage().contains("Password"));
    }

    @Test
    @DisplayName("Should throw AttributeCannotBeEmptyException when password is blank or whitespace")
    void shouldThrowWhenPasswordIsBlank() {
        AttributeCannotBeEmptyException ex = assertThrows(AttributeCannotBeEmptyException.class, () -> new Password("   "));
        assertTrue(ex.getMessage().contains("Password"));
    }

    @Test
    @DisplayName("Should instantiate Password via fromHash static factory")
    void shouldInstantiateFromHashFactory() {
        String hash = "$argon2id$v=19$m=16384,t=2,p=1$sampleSalt$sampleHash";
        Password password = Password.fromHash(hash);

        assertNotNull(password);
        assertEquals(hash, password.value());
    }
}
