package garcias.api.catalog.product.domain.entities;

import garcias.api.catalog.category.domain.valueobjects.CategoryId;
import garcias.api.catalog.category.domain.valueobjects.CategoryName;
import garcias.api.catalog.product.domain.enums.ProductStatus;
import garcias.api.catalog.product.domain.valueobjects.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Product Domain Entity Unit Tests")
class ProductTest {

    private final ProductId productId = new ProductId(1L);
    private final ProductName productName = new ProductName("Bolo de Chocolate");
    private final ProductWeight productWeight = new ProductWeight(new BigDecimal("1.200"));
    private final CatalogPosition catalogPosition = new CatalogPosition(1L);
    private final CategoryId categoryId = new CategoryId(10L);
    private final CategoryName categoryName = new CategoryName("Bolos");
    private final ProductPhoto productPhoto = new ProductPhoto("bolo.webp");

    @Test
    @DisplayName("Deve instanciar Product via construtor com sucesso")
    void shouldConstructProductSuccessfully() {
        Product product = new Product(
                productId,
                productName,
                productWeight,
                catalogPosition,
                categoryId,
                categoryName,
                ProductStatus.ACTIVE,
                productPhoto
        );

        assertThat(product.getId()).isEqualTo(productId);
        assertThat(product.getName()).isEqualTo(productName);
        assertThat(product.getWeight()).isEqualTo(productWeight);
        assertThat(product.getPosition()).isEqualTo(catalogPosition);
        assertThat(product.getCategoryId()).isEqualTo(categoryId);
        assertThat(product.getCategoryName()).isEqualTo(categoryName);
        assertThat(product.getStatus()).isEqualTo(ProductStatus.ACTIVE);
        assertThat(product.getPhoto()).isEqualTo(productPhoto);
        assertThat(product.isActive()).isTrue();
    }

    @Test
    @DisplayName("Deve criar Product via método factory create com status ACTIVE e Id vazio")
    void shouldCreateProductViaFactory() {
        Product product = Product.create(
                productName,
                productWeight,
                catalogPosition,
                productPhoto,
                categoryId
        );

        assertThat(product.getId().isEmpty()).isTrue();
        assertThat(product.getName()).isEqualTo(productName);
        assertThat(product.getWeight()).isEqualTo(productWeight);
        assertThat(product.getPosition()).isEqualTo(catalogPosition);
        assertThat(product.getPhoto()).isEqualTo(productPhoto);
        assertThat(product.getCategoryId()).isEqualTo(categoryId);
        assertThat(product.getCategoryName()).isNull();
        assertThat(product.getStatus()).isEqualTo(ProductStatus.ACTIVE);
        assertThat(product.isActive()).isTrue();
    }

    @Test
    @DisplayName("Deve validar campos obrigatórios no construtor lançando NullPointerException")
    void shouldValidateRequiredFieldsInConstructor() {
        assertThatThrownBy(() -> new Product(null, productName, productWeight, catalogPosition, categoryId, categoryName, ProductStatus.ACTIVE, productPhoto))
                .isInstanceOf(NullPointerException.class);

        assertThatThrownBy(() -> new Product(productId, null, productWeight, catalogPosition, categoryId, categoryName, ProductStatus.ACTIVE, productPhoto))
                .isInstanceOf(NullPointerException.class);

        assertThatThrownBy(() -> new Product(productId, productName, null, catalogPosition, categoryId, categoryName, ProductStatus.ACTIVE, productPhoto))
                .isInstanceOf(NullPointerException.class);

        assertThatThrownBy(() -> new Product(productId, productName, productWeight, null, categoryId, categoryName, ProductStatus.ACTIVE, productPhoto))
                .isInstanceOf(NullPointerException.class);

        assertThatThrownBy(() -> new Product(productId, productName, productWeight, catalogPosition, null, categoryName, ProductStatus.ACTIVE, productPhoto))
                .isInstanceOf(NullPointerException.class);

        assertThatThrownBy(() -> new Product(productId, productName, productWeight, catalogPosition, categoryId, categoryName, null, productPhoto))
                .isInstanceOf(NullPointerException.class);

        assertThatThrownBy(() -> new Product(productId, productName, productWeight, catalogPosition, categoryId, categoryName, ProductStatus.ACTIVE, null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("Deve alterar status para ACTIVE e INACTIVE com activate, deactivate e changeStatus")
    void shouldChangeStatusCorrectly() {
        Product product = Product.create(productName, productWeight, catalogPosition, productPhoto, categoryId);

        product.deactivate();
        assertThat(product.getStatus()).isEqualTo(ProductStatus.INACTIVE);
        assertThat(product.isActive()).isFalse();

        product.activate();
        assertThat(product.getStatus()).isEqualTo(ProductStatus.ACTIVE);
        assertThat(product.isActive()).isTrue();

        product.changeStatus(ProductStatus.INACTIVE);
        assertThat(product.getStatus()).isEqualTo(ProductStatus.INACTIVE);

        product.changeStatus(ProductStatus.ACTIVE);
        assertThat(product.getStatus()).isEqualTo(ProductStatus.ACTIVE);
    }

    @Test
    @DisplayName("Deve renomear produto e validar nulo")
    void shouldRenameProductAndValidateNull() {
        Product product = Product.create(productName, productWeight, catalogPosition, productPhoto, categoryId);
        ProductName newName = new ProductName("Bolo de Morango");

        product.rename(newName);
        assertThat(product.getName()).isEqualTo(newName);

        assertThatThrownBy(() -> product.rename(null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("Deve alterar peso e validar nulo")
    void shouldChangeWeightAndValidateNull() {
        Product product = Product.create(productName, productWeight, catalogPosition, productPhoto, categoryId);
        ProductWeight newWeight = new ProductWeight(new BigDecimal("2.500"));

        product.changeWeight(newWeight);
        assertThat(product.getWeight()).isEqualTo(newWeight);

        assertThatThrownBy(() -> product.changeWeight(null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("Deve alterar posição no catálogo e validar nulo")
    void shouldChangePositionAndValidateNull() {
        Product product = Product.create(productName, productWeight, catalogPosition, productPhoto, categoryId);
        CatalogPosition newPosition = new CatalogPosition(10L);

        product.changePosition(newPosition);
        assertThat(product.getPosition()).isEqualTo(newPosition);

        assertThatThrownBy(() -> product.changePosition(null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("Deve alterar foto e validar nulo")
    void shouldChangePhotoAndValidateNull() {
        Product product = Product.create(productName, productWeight, catalogPosition, productPhoto, categoryId);
        ProductPhoto newPhoto = new ProductPhoto("novo-bolo.webp");

        product.changePhoto(newPhoto);
        assertThat(product.getPhoto()).isEqualTo(newPhoto);

        assertThatThrownBy(() -> product.changePhoto(null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("Deve alterar categoria e validar nulo")
    void shouldChangeCategoryAndValidateNull() {
        Product product = Product.create(productName, productWeight, catalogPosition, productPhoto, categoryId);
        CategoryId newCat = new CategoryId(20L);

        product.changeCategory(newCat);
        assertThat(product.getCategoryId()).isEqualTo(newCat);

        assertThatThrownBy(() -> product.changeCategory(null)).isInstanceOf(NullPointerException.class);
    }
}
