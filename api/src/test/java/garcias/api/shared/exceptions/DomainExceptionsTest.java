package garcias.api.shared.exceptions;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Domain Exceptions (Shared) Unit Tests")
class DomainExceptionsTest {

    @Test
    @DisplayName("Deve instanciar AttributeCannotBeEmptyException")
    void shouldInstantiateAttributeCannotBeEmptyException() {
        AttributeCannotBeEmptyException ex = new AttributeCannotBeEmptyException("Name");
        assertThat(ex.getMessage()).isEqualTo("Name cannot be empty");
    }

    @Test
    @DisplayName("Deve instanciar AttributeMustBeGreaterThanZeroException")
    void shouldInstantiateAttributeMustBeGreaterThanZeroException() {
        AttributeMustBeGreaterThanZeroException ex = new AttributeMustBeGreaterThanZeroException("Weight", -5);
        assertThat(ex.getMessage()).isEqualTo("Weight must be greater than zero. Received: -5");
    }

    @Test
    @DisplayName("Deve instanciar AttributeTooLongException")
    void shouldInstantiateAttributeTooLongException() {
        AttributeTooLongException ex = new AttributeTooLongException("Description", "50");
        assertThat(ex.getMessage()).isEqualTo("Description cannot exceed 50 characters.");
    }

    @Test
    @DisplayName("Deve instanciar ValueObjectCannotBeNullException")
    void shouldInstantiateValueObjectCannotBeNullException() {
        ValueObjectCannotBeNullException ex = new ValueObjectCannotBeNullException("ProductVO");
        assertThat(ex.getMessage()).isEqualTo("ProductVO cannot be null.");
    }

    @Test
    @DisplayName("Deve instanciar SuperAdminAlreadyExistsException")
    void shouldInstantiateSuperAdminAlreadyExistsException() {
        SuperAdminAlreadyExistsException ex = new SuperAdminAlreadyExistsException();
        assertThat(ex.getMessage()).contains("SUPER_ADMIN user");
    }

    @Test
    @DisplayName("Deve instanciar SuperAdminCreationNotAllowedException")
    void shouldInstantiateSuperAdminCreationNotAllowedException() {
        SuperAdminCreationNotAllowedException ex = new SuperAdminCreationNotAllowedException();
        assertThat(ex.getMessage()).contains("Creating a SUPER_ADMIN user is not allowed");
    }

    @Test
    @DisplayName("Deve instanciar SuperAdminModificationNotAllowedException")
    void shouldInstantiateSuperAdminModificationNotAllowedException() {
        SuperAdminModificationNotAllowedException ex = new SuperAdminModificationNotAllowedException();
        assertThat(ex.getMessage()).contains("Modifying the role to/from SUPER_ADMIN is not allowed");
    }

    @Test
    @DisplayName("Deve instanciar ObjectAlreadyExistsException com chave e valor")
    void shouldInstantiateObjectAlreadyExistsException() {
        ObjectAlreadyExistsException ex = new ObjectAlreadyExistsException("Category", "Padaria");
        assertThat(ex.getMessage()).isEqualTo("Category already exists: Padaria");
    }

    @Test
    @DisplayName("Deve instanciar ObjectNotFoundException")
    void shouldInstantiateObjectNotFoundException() {
        ObjectNotFoundException ex = new ObjectNotFoundException(42L);
        assertThat(ex.getMessage()).isEqualTo("Object with id 42 not found.");
    }

    @Test
    @DisplayName("Deve instanciar InvalidOriginException")
    void shouldInstantiateInvalidOriginException() {
        InvalidOriginException ex = new InvalidOriginException("https://evil.com");
        assertThat(ex.getMessage()).isEqualTo("https://evil.com");
    }

    @Test
    @DisplayName("Deve instanciar RateLimitExceededException")
    void shouldInstantiateRateLimitExceededException() {
        RateLimitExceededException ex = new RateLimitExceededException(60L);
        assertThat(ex.getRetryAfterSeconds()).isEqualTo(60L);
        assertThat(ex.getMessage()).contains("60 segundos");
    }
}
