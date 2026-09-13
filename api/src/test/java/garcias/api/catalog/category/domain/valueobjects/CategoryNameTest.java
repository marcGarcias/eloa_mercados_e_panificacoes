package garcias.api.catalog.category.domain.valueobjects;

import garcias.api.shared.exceptions.AttributeCannotBeEmptyException;
import garcias.api.shared.exceptions.AttributeTooLongException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("CategoryName Value Object Unit Tests")
class CategoryNameTest {

    @Test
    @DisplayName("Deve criar CategoryName válido")
    void shouldCreateCategoryNameWithValidValue() {
        CategoryName name = new CategoryName("Padaria & Confeitaria");

        assertThat(name.value()).isEqualTo("Padaria & Confeitaria");
    }

    @Test
    @DisplayName("Deve lançar NullPointerException quando valor for nulo")
    void shouldThrowWhenValueIsNull() {
        assertThatThrownBy(() -> new CategoryName(null))
                .isInstanceOf(NullPointerException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "\t", "\n"})
    @DisplayName("Deve lançar AttributeCannotBeEmptyException quando nome estiver em branco")
    void shouldThrowWhenValueIsBlank(String blankName) {
        assertThatThrownBy(() -> new CategoryName(blankName))
                .isInstanceOf(AttributeCannotBeEmptyException.class)
                .hasMessageContaining("Category name");
    }

    @Test
    @DisplayName("Deve lançar AttributeTooLongException quando nome ultrapassar 50 caracteres")
    void shouldThrowWhenValueExceeds50Characters() {
        String longName = "a".repeat(51);

        assertThatThrownBy(() -> new CategoryName(longName))
                .isInstanceOf(AttributeTooLongException.class)
                .hasMessageContaining("Category name");
    }

    @Test
    @DisplayName("Deve aceitar nome no limite de 50 caracteres")
    void shouldAcceptNameWithExactly50Characters() {
        String exactName = "a".repeat(50);
        CategoryName name = new CategoryName(exactName);

        assertThat(name.value()).hasSize(50);
    }
}
