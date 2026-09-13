package garcias.api.identity.user.domain.exceptions;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("UserAlreadyExistsException Unit Tests")
class UserAlreadyExistsExceptionTest {

    @Test
    @DisplayName("Deve instanciar UserAlreadyExistsException com mensagem")
    void shouldInstantiateUserAlreadyExistsException() {
        UserAlreadyExistsException ex = new UserAlreadyExistsException("Usuário já cadastrado.");
        assertThat(ex.getMessage()).isEqualTo("Usuário já cadastrado.");
    }
}
