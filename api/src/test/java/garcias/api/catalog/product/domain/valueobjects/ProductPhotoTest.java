package garcias.api.catalog.product.domain.valueobjects;

import garcias.api.shared.exceptions.AttributeCannotBeEmptyException;
import garcias.api.shared.exceptions.ValueObjectCannotBeNullException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("ProductPhoto Value Object Unit Tests")
class ProductPhotoTest {

    @Test
    @DisplayName("Deve criar ProductPhoto com valor válido")
    void shouldCreateProductPhotoWithValidValue() {
        ProductPhoto photo = new ProductPhoto("uploads/pao-frances.webp");

        assertThat(photo.value()).isEqualTo("uploads/pao-frances.webp");
    }

    @Test
    @DisplayName("Deve lançar ValueObjectCannotBeNullException quando valor for nulo")
    void shouldThrowWhenValueIsNull() {
        assertThatThrownBy(() -> new ProductPhoto(null))
                .isInstanceOf(ValueObjectCannotBeNullException.class)
                .hasMessageContaining("ProductPhoto");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "\t", "\n"})
    @DisplayName("Deve lançar AttributeCannotBeEmptyException quando valor for em branco")
    void shouldThrowWhenValueIsBlank(String blankValue) {
        assertThatThrownBy(() -> new ProductPhoto(blankValue))
                .isInstanceOf(AttributeCannotBeEmptyException.class)
                .hasMessageContaining("ProductPhoto");
    }
}
