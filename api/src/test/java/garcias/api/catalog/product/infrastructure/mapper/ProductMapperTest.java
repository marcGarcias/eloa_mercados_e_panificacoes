package garcias.api.catalog.product.infrastructure.mapper;

import garcias.api.catalog.category.domain.valueobjects.CategoryId;
import garcias.api.catalog.category.infrastructure.persistence.CategoryJpaEntity;
import garcias.api.catalog.product.domain.entities.Product;
import garcias.api.catalog.product.domain.enums.ProductStatus;
import garcias.api.catalog.product.domain.valueobjects.*;
import garcias.api.catalog.product.infrastructure.persistence.ProductJpaEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ProductMapper and ProductJpaEntity Unit Tests")
class ProductMapperTest {

    @Test
    @DisplayName("Deve mapear Product com ID vazio para ProductJpaEntity com create()")
    void shouldMapProductWithEmptyIdToJpaEntity() {
        Product product = Product.create(
                new ProductName("Pão Francês"),
                new ProductWeight(new BigDecimal("0.050")),
                new CatalogPosition(1L),
                new ProductPhoto("pao.webp"),
                new CategoryId(10L)
        );

        ProductJpaEntity entity = ProductMapper.toEntity(product);

        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isNull();
        assertThat(entity.getName()).isEqualTo("Pão Francês");
        assertThat(entity.getWeight()).isEqualByComparingTo("0.050");
        assertThat(entity.getPosition()).isEqualTo(1L);
        assertThat(entity.getPhoto()).isEqualTo("pao.webp");
        assertThat(entity.getStatus()).isEqualTo(ProductStatus.ACTIVE);
        assertThat(entity.getCategory()).isNotNull();
        assertThat(entity.getCategory().getId()).isEqualTo(10L);
    }

    @Test
    @DisplayName("Deve mapear Product com ID existente para ProductJpaEntity com withId()")
    void shouldMapProductWithExistingIdToJpaEntity() {
        Product product = new Product(
                new ProductId(99L),
                new ProductName("Bolo de Fubá"),
                new ProductWeight(new BigDecimal("0.750")),
                new CatalogPosition(3L),
                new CategoryId(5L),
                null,
                ProductStatus.INACTIVE,
                new ProductPhoto("fuba.webp")
        );

        ProductJpaEntity entity = ProductMapper.toEntity(product);

        assertThat(entity.getId()).isEqualTo(99L);
        assertThat(entity.getName()).isEqualTo("Bolo de Fubá");
        assertThat(entity.getStatus()).isEqualTo(ProductStatus.INACTIVE);
    }

    @Test
    @DisplayName("Deve mapear ProductJpaEntity para Domain Product com toDomain()")
    void shouldMapJpaEntityToDomainProduct() {
        CategoryJpaEntity categoryEntity = CategoryJpaEntity.withId(5L, "Bolos");
        ProductJpaEntity entity = ProductJpaEntity.withId(
                42L,
                "Bolo de Cenoura",
                new BigDecimal("0.800"),
                2L,
                "cenoura.webp",
                categoryEntity,
                ProductStatus.ACTIVE
        );

        Product domain = ProductMapper.toDomain(entity);

        assertThat(domain).isNotNull();
        assertThat(domain.getId().value()).isEqualTo(42L);
        assertThat(domain.getName().value()).isEqualTo("Bolo de Cenoura");
        assertThat(domain.getWeight().value()).isEqualByComparingTo("0.800");
        assertThat(domain.getPosition().value()).isEqualTo(2L);
        assertThat(domain.getPhoto().value()).isEqualTo("cenoura.webp");
        assertThat(domain.getCategoryId().value()).isEqualTo(5L);
        assertThat(domain.getCategoryName().value()).isEqualTo("Bolos");
        assertThat(domain.getStatus()).isEqualTo(ProductStatus.ACTIVE);
    }
}
