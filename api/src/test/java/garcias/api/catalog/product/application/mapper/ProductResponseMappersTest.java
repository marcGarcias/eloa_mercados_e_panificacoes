package garcias.api.catalog.product.application.mapper;

import garcias.api.catalog.category.domain.valueobjects.CategoryId;
import garcias.api.catalog.category.domain.valueobjects.CategoryName;
import garcias.api.catalog.product.application.dto.responses.ProductAdminResponse;
import garcias.api.catalog.product.application.dto.responses.ProductPublicResponse;
import garcias.api.catalog.product.domain.entities.Product;
import garcias.api.catalog.product.domain.enums.ProductStatus;
import garcias.api.catalog.product.domain.valueobjects.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Product Response Mappers Unit Tests")
class ProductResponseMappersTest {

    private Product createProduct() {
        return new Product(
                new ProductId(10L),
                new ProductName("Bolo de Fubá"),
                new ProductWeight(new BigDecimal("0.800")),
                new CatalogPosition(2L),
                new CategoryId(1L),
                new CategoryName("Bolos Tradicionais"),
                ProductStatus.ACTIVE,
                new ProductPhoto("fuba.webp")
        );
    }

    @Test
    @DisplayName("Deve mapear Product para ProductAdminResponse")
    void shouldMapToProductAdminResponse() {
        Product product = createProduct();

        ProductAdminResponse response = ProductAdminResponseMapper.toResponse(product);

        assertThat(response.id()).isEqualTo(10L);
        assertThat(response.name()).isEqualTo("Bolo de Fubá");
        assertThat(response.weight()).isEqualByComparingTo("0.800");
        assertThat(response.position()).isEqualTo(2L);
        assertThat(response.photo()).isEqualTo("fuba.webp");
        assertThat(response.categoryName()).isEqualTo("Bolos Tradicionais");
        assertThat(response.status()).isEqualTo(ProductStatus.ACTIVE);
    }

    @Test
    @DisplayName("Deve mapear Product para ProductPublicResponse")
    void shouldMapToProductPublicResponse() {
        Product product = createProduct();

        ProductPublicResponse response = ProductPublicResponseMapper.toResponse(product);

        assertThat(response.name()).isEqualTo("Bolo de Fubá");
        assertThat(response.weight()).isEqualByComparingTo("0.800");
        assertThat(response.photoUrl()).isEqualTo("fuba.webp");
        assertThat(response.categoryName()).isEqualTo("Bolos Tradicionais");
        assertThat(response.position()).isEqualTo(2L);
    }
}
