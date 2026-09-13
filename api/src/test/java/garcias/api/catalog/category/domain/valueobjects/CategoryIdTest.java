package garcias.api.catalog.category.domain.valueobjects;

import garcias.api.shared.exceptions.ValueObjectCannotBeNullException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("CategoryId Value Object Unit Tests")
class CategoryIdTest {

    @Test
    @DisplayName("Deve criar CategoryId com valor válido")
    void shouldCreateCategoryIdWithValidValue() {
        CategoryId id = new CategoryId(10L);

        assertThat(id.value()).isEqualTo(10L);
        assertThat(id.isEmpty()).isFalse();
    }

    @Test
    @DisplayName("Deve lançar exceção quando valor for nulo")
    void shouldThrowWhenValueIsNull() {
        assertThatThrownBy(() -> new CategoryId(null))
                .isInstanceOf(ValueObjectCannotBeNullException.class)
                .hasMessageContaining("CategoryId");
    }

    @Test
    @DisplayName("Deve criar CategoryId vazio com valor 0")
    void shouldCreateEmptyCategoryId() {
        CategoryId emptyId = CategoryId.empty();

        assertThat(emptyId.value()).isEqualTo(0L);
        assertThat(emptyId.isEmpty()).isTrue();
    }
}
