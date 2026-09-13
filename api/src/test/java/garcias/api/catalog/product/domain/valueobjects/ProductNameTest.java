package garcias.api.catalog.product.domain.valueobjects;

import garcias.api.shared.exceptions.AttributeCannotBeEmptyException;
import garcias.api.shared.exceptions.AttributeTooLongException;
import garcias.api.shared.exceptions.ValueObjectCannotBeNullException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("ProductName Value Object Unit Tests")
class ProductNameTest {

    @Test
    @DisplayName("Deve criar ProductName com valor válido")
    void shouldCreateProductNameWithValidValue() {
        ProductName name = new ProductName("Pão Francês Tradicional");

        assertThat(name.value()).isEqualTo("Pão Francês Tradicional");
    }

    @Test
    @DisplayName("Deve lançar ValueObjectCannotBeNullException quando valor for nulo")
    void shouldThrowWhenValueIsNull() {
        assertThatThrownBy(() -> new ProductName(null))
                .isInstanceOf(ValueObjectCannotBeNullException.class)
                .hasMessageContaining("Product name");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "\t", "\n"})
    @DisplayName("Deve lançar AttributeCannotBeEmptyException quando valor for em branco")
    void shouldThrowWhenValueIsBlank(String blankValue) {
        assertThatThrownBy(() -> new ProductName(blankValue))
                .isInstanceOf(AttributeCannotBeEmptyException.class)
                .hasMessageContaining("Product name");
    }

    @Test
    @DisplayName("Deve lançar AttributeTooLongException quando valor exceder 120 caracteres")
    void shouldThrowWhenValueExceeds120Characters() {
        String longName = "p".repeat(121);

        assertThatThrownBy(() -> new ProductName(longName))
                .isInstanceOf(AttributeTooLongException.class)
                .hasMessageContaining("Product name");
    }

    @Test
    @DisplayName("Deve aceitar nome com exatamente 120 caracteres")
    void shouldAcceptNameWithExactly120Characters() {
        String exactName = "p".repeat(120);
        ProductName name = new ProductName(exactName);

        assertThat(name.value()).hasSize(120);
    }
}
