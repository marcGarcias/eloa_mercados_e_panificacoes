package garcias.api.catalog.product.domain.valueobjects;

import garcias.api.shared.exceptions.AttributeCannotBeEmptyException;
import garcias.api.shared.exceptions.AttributeTooLongException;
import garcias.api.shared.exceptions.AttributeTooShortException;
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
        ProductName name = new ProductName("Pão Francês");

        assertThat(name.value()).isEqualTo("Pão Francês");
    }

    @Test
    @DisplayName("Deve lançar ValueObjectCannotBeNullException quando valor for nulo")
    void shouldThrowWhenValueIsNull() {
        assertThatThrownBy(() -> new ProductName(null))
                .isInstanceOf(ValueObjectCannotBeNullException.class)
                .hasMessageContaining("Nome do produto");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "\t", "\n"})
    @DisplayName("Deve lançar AttributeCannotBeEmptyException quando valor for em branco")
    void shouldThrowWhenValueIsBlank(String blankValue) {
        assertThatThrownBy(() -> new ProductName(blankValue))
                .isInstanceOf(AttributeCannotBeEmptyException.class)
                .hasMessageContaining("Nome do produto");
    }

    @Test
    @DisplayName("Deve lançar AttributeTooShortException quando valor tiver menos de 2 caracteres")
    void shouldThrowWhenValueIsShorterThan2Characters() {
        assertThatThrownBy(() -> new ProductName("P"))
                .isInstanceOf(AttributeTooShortException.class)
                .hasMessageContaining("Nome do produto");
    }

    @Test
    @DisplayName("Deve lançar AttributeTooLongException quando valor exceder 32 caracteres")
    void shouldThrowWhenValueExceeds32Characters() {
        String longName = "p".repeat(33);

        assertThatThrownBy(() -> new ProductName(longName))
                .isInstanceOf(AttributeTooLongException.class)
                .hasMessageContaining("Nome do produto");
    }

    @Test
    @DisplayName("Deve aceitar nome com exatamente 32 caracteres")
    void shouldAcceptNameWithExactly32Characters() {
        String exactName = "p".repeat(32);
        ProductName name = new ProductName(exactName);

        assertThat(name.value()).hasSize(32);
    }
}
