package garcias.api.identity.user.domain.valueobjects;

import garcias.api.shared.exceptions.AttributeCannotBeEmptyException;
import garcias.api.shared.exceptions.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("UserCode Value Object Unit Tests")
class UserCodeTest {

    @Test
    @DisplayName("Deve criar UserCode válido com dígitos e pelo menos 4 caracteres")
    void shouldCreateValidUserCode() {
        UserCode code = new UserCode("0001");

        assertThat(code.value()).isEqualTo("0001");
    }

    @Test
    @DisplayName("Deve formatar número com preenchimento de zeros usando from()")
    void shouldFormatNumberWithFromMethod() {
        UserCode code1 = UserCode.from(7L);
        UserCode code2 = UserCode.from(12345L);

        assertThat(code1.value()).isEqualTo("0007");
        assertThat(code2.value()).isEqualTo("12345");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "\t"})
    @DisplayName("Deve lançar AttributeCannotBeEmptyException quando estiver em branco ou nulo")
    void shouldThrowWhenBlank(String blank) {
        assertThatThrownBy(() -> new UserCode(blank))
                .isInstanceOf(AttributeCannotBeEmptyException.class)
                .hasMessageContaining("User code");
    }

    @Test
    @DisplayName("Deve lançar AttributeCannotBeEmptyException quando for nulo")
    void shouldThrowWhenNull() {
        assertThatThrownBy(() -> new UserCode(null))
                .isInstanceOf(AttributeCannotBeEmptyException.class)
                .hasMessageContaining("User code");
    }

    @ParameterizedTest
    @ValueSource(strings = {"abcd", "12a3", "12-34", "12 34"})
    @DisplayName("Deve lançar DomainException quando contiver caracteres não numéricos")
    void shouldThrowWhenNonNumeric(String nonNumeric) {
        assertThatThrownBy(() -> new UserCode(nonNumeric))
                .isInstanceOf(DomainException.class)
                .hasMessage("User code must contain only numbers");
    }

    @ParameterizedTest
    @ValueSource(strings = {"1", "12", "123"})
    @DisplayName("Deve lançar DomainException quando tiver menos de 4 dígitos")
    void shouldThrowWhenLessThan4Digits(String shortCode) {
        assertThatThrownBy(() -> new UserCode(shortCode))
                .isInstanceOf(DomainException.class)
                .hasMessage("User code must have at least 4 digits");
    }
}
