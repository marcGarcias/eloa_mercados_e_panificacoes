package garcias.api.identity.authentication.domain.exceptions;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Authentication Domain Exceptions Unit Tests")
class AuthenticationExceptionsTest {

    @Test
    @DisplayName("Deve instanciar InvalidCredentialsException com mensagem padrão")
    void shouldInstantiateInvalidCredentialsException() {
        InvalidCredentialsException ex = new InvalidCredentialsException();
        assertThat(ex.getMessage()).isEqualTo("Invalid credentials");
    }

    @Test
    @DisplayName("Deve instanciar InvalidSetupAccessKeyException com mensagem informada")
    void shouldInstantiateInvalidSetupAccessKeyException() {
        InvalidSetupAccessKeyException ex = new InvalidSetupAccessKeyException("Chave de setup incorreta");
        assertThat(ex.getMessage()).isEqualTo("Chave de setup incorreta");
    }

    @Test
    @DisplayName("Deve instanciar InvalidSetupCpfException com mensagem padrão")
    void shouldInstantiateInvalidSetupCpfException() {
        InvalidSetupCpfException ex = new InvalidSetupCpfException();
        assertThat(ex.getMessage()).contains("CPF de setup");
    }

    @Test
    @DisplayName("Deve instanciar MissingRefreshTokenException com mensagem padrão")
    void shouldInstantiateMissingRefreshTokenException() {
        MissingRefreshTokenException ex = new MissingRefreshTokenException();
        assertThat(ex.getMessage()).isEqualTo("Refresh token is missing.");
    }
}
