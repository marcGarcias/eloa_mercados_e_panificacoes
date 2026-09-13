package garcias.api.catalog.product.application.services.product;

import garcias.api.catalog.category.domain.entities.Category;
import garcias.api.catalog.category.domain.persistence.CategoryRepository;
import garcias.api.catalog.category.domain.valueobjects.CategoryId;
import garcias.api.catalog.category.domain.valueobjects.CategoryName;
import garcias.api.catalog.product.application.dto.requests.CreateProductRequest;
import garcias.api.catalog.product.application.storage.ImageStorage;
import garcias.api.catalog.product.application.validation.WebpImageValidator;
import garcias.api.catalog.product.domain.entities.Product;
import garcias.api.catalog.product.domain.enums.ProductStatus;
import garcias.api.catalog.product.domain.repositories.ProductRepository;
import garcias.api.catalog.product.domain.valueobjects.*;
import garcias.api.shared.exceptions.InvalidImageException;
import garcias.api.shared.exceptions.ObjectNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreateProductService Unit Tests")
class CreateProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ApplicationEventPublisher publisher;

    @Mock
    private WebpImageValidator webpImageValidator;

    @Mock
    private ImageStorage imageStorage;

    @Mock
    private MultipartFile photoFile;

    @InjectMocks
    private CreateProductService createProductService;

    @Test
    @DisplayName("Deve criar primeiro produto com posição 1 quando não houver produtos")
    void shouldCreateFirstProductWithPosition1() {
        CategoryId categoryId = new CategoryId(10L);
        Category category = Category.create(categoryId, new CategoryName("Padaria"));

        CreateProductRequest request = new CreateProductRequest(
                "Pão Francês",
                new BigDecimal("0.050"),
                photoFile,
                10L
        );

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        doNothing().when(webpImageValidator).validate(photoFile);
        when(imageStorage.save(photoFile)).thenReturn("uploads/pao.webp");
        when(productRepository.findLastPosition()).thenReturn(Optional.empty());

        Product savedProduct = new Product(
                new ProductId(1L),
                new ProductName("Pão Francês"),
                new ProductWeight(new BigDecimal("0.050")),
                CatalogPosition.first(),
                categoryId,
                null,
                ProductStatus.ACTIVE,
                new ProductPhoto("uploads/pao.webp")
        );
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        Product result = createProductService.execute(request);

        assertThat(result).isNotNull();
        assertThat(result.getPosition().value()).isEqualTo(1L);

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(captor.capture());
        assertThat(captor.getValue().getPosition().value()).isEqualTo(1L);
        assertThat(captor.getValue().getPhoto().value()).isEqualTo("uploads/pao.webp");
    }

    @Test
    @DisplayName("Deve criar próximo produto com posição incrementada quando já houver produtos")
    void shouldCreateNextProductWithIncrementedPosition() {
        CategoryId categoryId = new CategoryId(10L);
        Category category = Category.create(categoryId, new CategoryName("Padaria"));

        CreateProductRequest request = new CreateProductRequest(
                "Croissant",
                new BigDecimal("0.100"),
                photoFile,
                10L
        );

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        doNothing().when(webpImageValidator).validate(photoFile);
        when(imageStorage.save(photoFile)).thenReturn("uploads/croissant.webp");
        when(productRepository.findLastPosition()).thenReturn(Optional.of(new CatalogPosition(4L)));

        Product savedProduct = new Product(
                new ProductId(2L),
                new ProductName("Croissant"),
                new ProductWeight(new BigDecimal("0.100")),
                new CatalogPosition(5L),
                categoryId,
                null,
                ProductStatus.ACTIVE,
                new ProductPhoto("uploads/croissant.webp")
        );
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        Product result = createProductService.execute(request);

        assertThat(result).isNotNull();
        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(captor.capture());
        assertThat(captor.getValue().getPosition().value()).isEqualTo(5L);
    }

    @Test
    @DisplayName("Deve lançar ObjectNotFoundException quando categoria não existir e não salvar imagem")
    void shouldThrowWhenCategoryNotFound() {
        CreateProductRequest request = new CreateProductRequest(
                "Pão", new BigDecimal("0.100"), photoFile, 999L
        );

        when(categoryRepository.findById(new CategoryId(999L))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> createProductService.execute(request))
                .isInstanceOf(ObjectNotFoundException.class);

        verify(imageStorage, never()).save(any());
        verify(productRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção de imagem inválida e não salvar produto nem imagem no storage")
    void shouldThrowWhenImageIsInvalid() {
        CategoryId categoryId = new CategoryId(10L);
        Category category = Category.create(categoryId, new CategoryName("Padaria"));
        CreateProductRequest request = new CreateProductRequest(
                "Pão", new BigDecimal("0.100"), photoFile, 10L
        );

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        doThrow(new InvalidImageException("Formato inválido")).when(webpImageValidator).validate(photoFile);

        assertThatThrownBy(() -> createProductService.execute(request))
                .isInstanceOf(InvalidImageException.class);

        verify(imageStorage, never()).save(any());
        verify(productRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve realizar rollback excluindo a imagem do storage caso ocorra erro ao salvar o produto")
    void shouldDeleteImageFromStorageWhenSaveProductFails() {
        CategoryId categoryId = new CategoryId(10L);
        Category category = Category.create(categoryId, new CategoryName("Padaria"));
        CreateProductRequest request = new CreateProductRequest(
                "Pão", new BigDecimal("0.100"), photoFile, 10L
        );

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(imageStorage.save(photoFile)).thenReturn("uploads/temp-pao.webp");
        when(productRepository.findLastPosition()).thenReturn(Optional.empty());
        when(productRepository.save(any(Product.class))).thenThrow(new RuntimeException("Database error"));

        assertThatThrownBy(() -> createProductService.execute(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Database error");

        verify(imageStorage).delete("uploads/temp-pao.webp");
    }
}
