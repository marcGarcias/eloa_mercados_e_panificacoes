package garcias.api.identity.user.domain.valueobjects;

import garcias.api.shared.exceptions.AttributeCannotBeEmptyException;
import garcias.api.shared.exceptions.AttributeTooLongException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("UserName Value Object Unit Tests")
class UserNameTest {

    @Test
    @DisplayName("Deve criar UserName válido e aplicar trim")
    void shouldCreateValidUserNameWithTrim() {
        UserName name = new UserName("  Carlos Eduardo  ");

        assertThat(name.value()).isEqualTo("Carlos Eduardo");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "\t", "\n"})
    @DisplayName("Deve lançar AttributeCannotBeEmptyException para valor em branco ou nulo")
    void shouldThrowWhenBlank(String blank) {
        assertThatThrownBy(() -> new UserName(blank))
                .isInstanceOf(AttributeCannotBeEmptyException.class)
                .hasMessageContaining("User name");
    }

    @Test
    @DisplayName("Deve lançar AttributeCannotBeEmptyException para valor nulo")
    void shouldThrowWhenNull() {
        assertThatThrownBy(() -> new UserName(null))
                .isInstanceOf(AttributeCannotBeEmptyException.class)
                .hasMessageContaining("User name");
    }

    @Test
    @DisplayName("Deve lançar AttributeTooLongException quando tamanho exceder 150 caracteres")
    void shouldThrowWhenTooLong() {
        String longName = "u".repeat(151);

        assertThatThrownBy(() -> new UserName(longName))
                .isInstanceOf(AttributeTooLongException.class)
                .hasMessageContaining("User name");
    }

    @Test
    @DisplayName("Deve aceitar nome no limite de 150 caracteres")
    void shouldAcceptExact150Characters() {
        String exactName = "u".repeat(150);
        UserName name = new UserName(exactName);

        assertThat(name.value()).hasSize(150);
    }
}
