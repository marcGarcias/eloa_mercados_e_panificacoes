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
        assertThat(ex.getMessage()).isEqualTo("Name não pode ficar em branco.");
    }

    @Test
    @DisplayName("Deve instanciar AttributeMustBeGreaterThanZeroException")
    void shouldInstantiateAttributeMustBeGreaterThanZeroException() {
        AttributeMustBeGreaterThanZeroException ex = new AttributeMustBeGreaterThanZeroException("Weight", -5);
        assertThat(ex.getMessage()).isEqualTo("Weight deve ser maior que zero.");
    }

    @Test
    @DisplayName("Deve instanciar AttributeTooLongException")
    void shouldInstantiateAttributeTooLongException() {
        AttributeTooLongException ex = new AttributeTooLongException("Description", "50");
        assertThat(ex.getMessage()).isEqualTo("Description não pode exceder 50 caracteres.");
    }

    @Test
    @DisplayName("Deve instanciar ValueObjectCannotBeNullException")
    void shouldInstantiateValueObjectCannotBeNullException() {
        ValueObjectCannotBeNullException ex = new ValueObjectCannotBeNullException("ProductVO");
        assertThat(ex.getMessage()).isEqualTo("ProductVO não pode ser nulo.");
    }

    @Test
    @DisplayName("Deve instanciar SuperAdminAlreadyExistsException")
    void shouldInstantiateSuperAdminAlreadyExistsException() {
        SuperAdminAlreadyExistsException ex = new SuperAdminAlreadyExistsException();
        assertThat(ex.getMessage()).contains("Proprietário");
    }

    @Test
    @DisplayName("Deve instanciar SuperAdminCreationNotAllowedException")
    void shouldInstantiateSuperAdminCreationNotAllowedException() {
        SuperAdminCreationNotAllowedException ex = new SuperAdminCreationNotAllowedException();
        assertThat(ex.getMessage()).contains("Proprietário");
    }

    @Test
    @DisplayName("Deve instanciar SuperAdminModificationNotAllowedException")
    void shouldInstantiateSuperAdminModificationNotAllowedException() {
        SuperAdminModificationNotAllowedException ex = new SuperAdminModificationNotAllowedException();
        assertThat(ex.getMessage()).contains("Proprietário");
    }

    @Test
    @DisplayName("Deve instanciar ObjectAlreadyExistsException com chave e valor")
    void shouldInstantiateObjectAlreadyExistsException() {
        ObjectAlreadyExistsException ex = new ObjectAlreadyExistsException("Category", "Padaria");
        assertThat(ex.getMessage()).isEqualTo("Já existe um registro cadastrado com o valor informado.");
    }

    @Test
    @DisplayName("Deve instanciar ObjectNotFoundException")
    void shouldInstantiateObjectNotFoundException() {
        ObjectNotFoundException ex = new ObjectNotFoundException(42L);
        assertThat(ex.getMessage()).isEqualTo("Registro não encontrado.");
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
