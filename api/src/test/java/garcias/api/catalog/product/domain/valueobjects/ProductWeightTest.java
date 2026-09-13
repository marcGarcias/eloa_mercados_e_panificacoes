package garcias.api.catalog.product.domain.valueobjects;

import garcias.api.shared.exceptions.AttributeMustBeGreaterThanZeroException;
import garcias.api.shared.exceptions.ValueObjectCannotBeNullException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("ProductWeight Value Object Unit Tests")
class ProductWeightTest {

    @Test
    @DisplayName("Deve criar ProductWeight com valor válido")
    void shouldCreateProductWeightWithValidValue() {
        ProductWeight weight = new ProductWeight(new BigDecimal("0.500"));

        assertThat(weight.value()).isEqualByComparingTo("0.500");
    }

    @Test
    @DisplayName("Deve lançar ValueObjectCannotBeNullException quando valor for nulo")
    void shouldThrowWhenValueIsNull() {
        assertThatThrownBy(() -> new ProductWeight(null))
                .isInstanceOf(ValueObjectCannotBeNullException.class)
                .hasMessageContaining("Product weight");
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "0.0", "-0.1", "-5.0"})
    @DisplayName("Deve lançar AttributeMustBeGreaterThanZeroException quando peso for <= 0")
    void shouldThrowWhenValueIsZeroOrNegative(String invalidValue) {
        BigDecimal val = new BigDecimal(invalidValue);
        assertThatThrownBy(() -> new ProductWeight(val))
                .isInstanceOf(AttributeMustBeGreaterThanZeroException.class)
                .hasMessageContaining("Product weight");
    }
}
