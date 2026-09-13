package garcias.api.catalog.product.domain.valueobjects;

import garcias.api.shared.exceptions.AttributeMustBeGreaterThanZeroException;
import garcias.api.shared.exceptions.ValueObjectCannotBeNullException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("CatalogPosition Value Object Unit Tests")
class CatalogPositionTest {

    @Test
    @DisplayName("Deve criar CatalogPosition com valor válido")
    void shouldCreateValidCatalogPosition() {
        CatalogPosition position = new CatalogPosition(5L);

        assertThat(position.value()).isEqualTo(5L);
    }

    @Test
    @DisplayName("Deve lançar ValueObjectCannotBeNullException quando valor for nulo")
    void shouldThrowWhenValueIsNull() {
        assertThatThrownBy(() -> new CatalogPosition(null))
                .isInstanceOf(ValueObjectCannotBeNullException.class)
                .hasMessageContaining("Catalog position");
    }

    @Test
    @DisplayName("Deve lançar AttributeMustBeGreaterThanZeroException quando valor for negativo")
    void shouldThrowWhenValueIsNegative() {
        assertThatThrownBy(() -> new CatalogPosition(-1L))
                .isInstanceOf(AttributeMustBeGreaterThanZeroException.class)
                .hasMessageContaining("Catalog position");
    }

    @Test
    @DisplayName("Deve calcular próxima posição com sucesso")
    void shouldCalculateNextPosition() {
        CatalogPosition current = new CatalogPosition(1L);
        CatalogPosition next = current.next();

        assertThat(next.value()).isEqualTo(2L);
    }

    @Test
    @DisplayName("Deve criar primeira posição com valor 1")
    void shouldCreateFirstPosition() {
        CatalogPosition first = CatalogPosition.first();

        assertThat(first.value()).isEqualTo(1L);
    }
}
