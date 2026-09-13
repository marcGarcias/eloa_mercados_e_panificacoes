package garcias.api.catalog.product.domain.valueobjects;

import garcias.api.shared.exceptions.AttributeMustBeGreaterThanZeroException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("ProductId Value Object Unit Tests")
class ProductIdTest {

    @Test
    @DisplayName("Deve criar ProductId com valor válido")
    void shouldCreateProductIdWithValidValue() {
        ProductId id = new ProductId(100L);

        assertThat(id.value()).isEqualTo(100L);
        assertThat(id.isEmpty()).isFalse();
    }

    @Test
    @DisplayName("Deve criar ProductId vazio")
    void shouldCreateEmptyProductId() {
        ProductId emptyId = ProductId.empty();

        assertThat(emptyId.value()).isNull();
        assertThat(emptyId.isEmpty()).isTrue();
    }

    @ParameterizedTest
    @ValueSource(longs = {0L, -1L, -100L})
    @DisplayName("Deve lançar AttributeMustBeGreaterThanZeroException quando valor for <= 0")
    void shouldThrowWhenValueIsZeroOrNegative(Long invalidValue) {
        assertThatThrownBy(() -> new ProductId(invalidValue))
                .isInstanceOf(AttributeMustBeGreaterThanZeroException.class)
                .hasMessageContaining("ProductId");
    }
}
