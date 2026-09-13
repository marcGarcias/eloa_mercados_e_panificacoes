package garcias.api.catalog.product.domain.valueobjects;

import garcias.api.catalog.category.domain.valueobjects.CategoryId;
import garcias.api.catalog.category.domain.valueobjects.CategoryName;
import garcias.api.catalog.product.domain.enums.ProductStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ProductFilter Value Object Unit Tests")
class ProductFilterTest {

    @Test
    @DisplayName("Deve retornar isEmpty true quando todos os campos forem nulos ou vazios")
    void shouldReturnIsEmptyTrueWhenAllFieldsEmpty() {
        ProductFilter filterAllNull = new ProductFilter(null, null, null, null);
        ProductFilter filterBlankName = new ProductFilter("   ", null, null, null);

        assertThat(filterAllNull.isEmpty()).isTrue();
        assertThat(filterBlankName.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("Deve retornar isEmpty false quando qualquer campo estiver preenchido")
    void shouldReturnIsEmptyFalseWhenAnyFieldIsPresent() {
        assertThat(new ProductFilter("Pão", null, null, null).isEmpty()).isFalse();
        assertThat(new ProductFilter(null, new CategoryId(1L), null, null).isEmpty()).isFalse();
        assertThat(new ProductFilter(null, null, new CategoryName("Doces"), null).isEmpty()).isFalse();
        assertThat(new ProductFilter(null, null, null, ProductStatus.ACTIVE).isEmpty()).isFalse();
    }
}
