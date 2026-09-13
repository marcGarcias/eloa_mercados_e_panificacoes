package garcias.api.catalog.product.application.services.product;

import garcias.api.catalog.category.domain.entities.Category;
import garcias.api.catalog.category.domain.persistence.CategoryRepository;
import garcias.api.catalog.category.domain.valueobjects.CategoryId;
import garcias.api.catalog.category.domain.valueobjects.CategoryName;
import garcias.api.catalog.product.application.dto.requests.UpdateProductRequest;
import garcias.api.catalog.product.application.storage.ImageStorage;
import garcias.api.catalog.product.domain.entities.Product;
import garcias.api.catalog.product.domain.enums.ProductStatus;
import garcias.api.catalog.product.domain.repositories.ProductRepository;
import garcias.api.catalog.product.domain.valueobjects.*;
import garcias.api.shared.exceptions.ObjectNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UpdateProductService Unit Tests")
class UpdateProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ImageStorage imageStorage;

    @Mock
    private MultipartFile newPhotoFile;

    @InjectMocks
    private UpdateProductService updateProductService;

    private Product createSampleProduct() {
        return new Product(
                new ProductId(1L),
                new ProductName("Pão Francês"),
                new ProductWeight(new BigDecimal("0.050")),
                new CatalogPosition(1L),
                new CategoryId(10L),
                new CategoryName("Padaria"),
                ProductStatus.ACTIVE,
                new ProductPhoto("uploads/old-photo.webp")
        );
    }

    @Test
    @DisplayName("Deve lançar ObjectNotFoundException quando produto não existir")
    void shouldThrowWhenProductNotFound() {
        ProductId id = new ProductId(99L);
        UpdateProductRequest request = new UpdateProductRequest(
                "Novo", new BigDecimal("1.000"), null, null, null, null
        );

        when(productRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> updateProductService.execute(id, request))
                .isInstanceOf(ObjectNotFoundException.class);
    }

    @Test
    @DisplayName("Deve atualizar dados cadastrais sem alterar foto")
    void shouldUpdateProductWithoutPhoto() {
        ProductId id = new ProductId(1L);
        Product product = createSampleProduct();

        UpdateProductRequest request = new UpdateProductRequest(
                "Pão Francês Especial",
                new BigDecimal("0.060"),
                null,
                null,
                ProductStatus.INACTIVE,
                null
        );

        when(productRepository.findById(id)).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);

        Product result = updateProductService.execute(id, request);

        assertThat(result.getName().value()).isEqualTo("Pão Francês Especial");
        assertThat(result.getWeight().value()).isEqualByComparingTo("0.060");
        assertThat(result.getStatus()).isEqualTo(ProductStatus.INACTIVE);
        assertThat(result.getPhoto().value()).isEqualTo("uploads/old-photo.webp");

        verify(imageStorage, never()).save(any());
        verify(imageStorage, never()).delete(any());
    }

    @Test
    @DisplayName("Não deve renomear produto quando nome for em branco")
    void shouldNotRenameWhenNameIsBlank() {
        ProductId id = new ProductId(1L);
        Product product = createSampleProduct();

        UpdateProductRequest request = new UpdateProductRequest(
                "   ", null, null, null, null, null
        );

        when(productRepository.findById(id)).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);

        Product result = updateProductService.execute(id, request);

        assertThat(result.getName().value()).isEqualTo("Pão Francês");
    }

    @Test
    @DisplayName("Não deve atualizar foto quando arquivo for vazio")
    void shouldNotUpdatePhotoWhenFileIsEmpty() {
        ProductId id = new ProductId(1L);
        Product product = createSampleProduct();

        when(newPhotoFile.isEmpty()).thenReturn(true);

        UpdateProductRequest request = new UpdateProductRequest(
                null, null, newPhotoFile, null, null, null
        );

        when(productRepository.findById(id)).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);

        Product result = updateProductService.execute(id, request);

        assertThat(result.getPhoto().value()).isEqualTo("uploads/old-photo.webp");
        verify(imageStorage, never()).save(any());
        verify(imageStorage, never()).delete(any());
    }

    @Test
    @DisplayName("Deve atualizar foto, salvando a nova e deletando a antiga")
    void shouldUpdatePhotoAndCleanUpOldImage() {
        ProductId id = new ProductId(1L);
        Product product = createSampleProduct();

        when(newPhotoFile.isEmpty()).thenReturn(false);
        when(imageStorage.save(newPhotoFile)).thenReturn("uploads/new-photo.webp");

        UpdateProductRequest request = new UpdateProductRequest(
                null, null, newPhotoFile, null, null, null
        );

        when(productRepository.findById(id)).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);

        Product result = updateProductService.execute(id, request);

        assertThat(result.getPhoto().value()).isEqualTo("uploads/new-photo.webp");
        verify(imageStorage).delete("uploads/old-photo.webp");
    }

    @Test
    @DisplayName("Deve atualizar categoria quando ela existir")
    void shouldUpdateCategoryWhenExists() {
        ProductId id = new ProductId(1L);
        Product product = createSampleProduct();
        CategoryId newCatId = new CategoryId(20L);
        Category newCategory = Category.create(newCatId, new CategoryName("Especiais"));

        UpdateProductRequest request = new UpdateProductRequest(
                null, null, null, 20L, null, null
        );

        when(productRepository.findById(id)).thenReturn(Optional.of(product));
        when(categoryRepository.findById(newCatId)).thenReturn(Optional.of(newCategory));
        when(productRepository.save(product)).thenReturn(product);

        Product result = updateProductService.execute(id, request);

        assertThat(result.getCategoryId()).isEqualTo(newCatId);
    }

    @Test
    @DisplayName("Deve lançar ObjectNotFoundException quando nova categoria informada não existir")
    void shouldThrowWhenNewCategoryDoesNotExist() {
        ProductId id = new ProductId(1L);
        Product product = createSampleProduct();
        CategoryId newCatId = new CategoryId(999L);

        UpdateProductRequest request = new UpdateProductRequest(
                null, null, null, 999L, null, null
        );

        when(productRepository.findById(id)).thenReturn(Optional.of(product));
        when(categoryRepository.findById(newCatId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> updateProductService.execute(id, request))
                .isInstanceOf(ObjectNotFoundException.class);

        verify(productRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve atualizar posição quando for diferente da atual")
    void shouldUpdatePositionWhenDifferent() {
        ProductId id = new ProductId(1L);
        Product product = createSampleProduct(); // position = 1

        UpdateProductRequest request = new UpdateProductRequest(
                null, null, null, null, null, 5L
        );

        when(productRepository.findById(id)).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);

        updateProductService.execute(id, request);

        verify(productRepository).updatePosition(product, new CatalogPosition(5L));
    }

    @Test
    @DisplayName("Não deve chamar updatePosition se a posição for igual à atual")
    void shouldNotCallUpdatePositionWhenSame() {
        ProductId id = new ProductId(1L);
        Product product = createSampleProduct(); // position = 1

        UpdateProductRequest request = new UpdateProductRequest(
                null, null, null, null, null, 1L
        );

        when(productRepository.findById(id)).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);

        updateProductService.execute(id, request);

        verify(productRepository, never()).updatePosition(any(), any());
    }
}
