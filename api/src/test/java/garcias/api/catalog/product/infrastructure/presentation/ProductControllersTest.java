package garcias.api.catalog.product.infrastructure.presentation;

import garcias.api.catalog.category.domain.valueobjects.CategoryId;
import garcias.api.catalog.category.domain.valueobjects.CategoryName;
import garcias.api.catalog.product.application.dto.requests.BatchDeleteProductsRequest;
import garcias.api.catalog.product.application.dto.requests.CreateProductRequest;
import garcias.api.catalog.product.application.dto.requests.PageRequestFilter;
import garcias.api.catalog.product.application.dto.requests.ReorderProductsRequest;
import garcias.api.catalog.product.application.dto.requests.UpdateProductRequest;
import garcias.api.catalog.product.application.dto.responses.ProductAdminResponse;
import garcias.api.catalog.product.application.dto.responses.ProductPublicResponse;
import garcias.api.catalog.product.application.usecases.image.LoadProductImageUseCase;
import garcias.api.catalog.product.application.usecases.product.BatchDeleteProductsUseCase;
import garcias.api.catalog.product.application.usecases.product.CreateProductUseCase;
import garcias.api.catalog.product.application.usecases.product.DeleteProductUseCase;
import garcias.api.catalog.product.application.usecases.product.ReorderProductsUseCase;
import garcias.api.catalog.product.application.usecases.product.SearchProductsUseCase;
import garcias.api.catalog.product.application.usecases.product.UpdateProductUseCase;
import garcias.api.catalog.product.application.validation.WebpImageValidator;
import garcias.api.catalog.product.domain.entities.Product;
import garcias.api.catalog.product.domain.enums.ProductStatus;
import garcias.api.catalog.product.domain.valueobjects.CatalogPosition;
import garcias.api.catalog.product.domain.valueobjects.ProductFilter;
import garcias.api.catalog.product.domain.valueobjects.ProductId;
import garcias.api.catalog.product.domain.valueobjects.ProductName;
import garcias.api.catalog.product.domain.valueobjects.ProductPhoto;
import garcias.api.catalog.product.domain.valueobjects.ProductWeight;
import garcias.api.catalog.product.infrastructure.presentation.admin.product.ProductAdminSearchController;
import garcias.api.catalog.product.infrastructure.presentation.admin.product.ProductCreateController;
import garcias.api.catalog.product.infrastructure.presentation.admin.product.ProductDeleteController;
import garcias.api.catalog.product.infrastructure.presentation.admin.product.ProductReorderController;
import garcias.api.catalog.product.infrastructure.presentation.admin.product.ProductUpdateController;
import garcias.api.catalog.product.infrastructure.presentation.shared.ProductImageController;
import garcias.api.catalog.product.infrastructure.presentation.web.product.ProductPublicSearchController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários dos Controllers de Produtos")
class ProductControllersTest {

    private Product createSampleProduct(Long id, String name, Long catId, String catName) {
        return new Product(
                new ProductId(id),
                new ProductName(name),
                new ProductWeight(new BigDecimal("0.500")),
                new CatalogPosition(1L),
                new CategoryId(catId),
                new CategoryName(catName),
                ProductStatus.ACTIVE,
                new ProductPhoto("foto.webp")
        );
    }

    @Nested
    @DisplayName("ProductPublicSearchController")
    class ProductPublicSearchControllerTests {
        @Mock
        private SearchProductsUseCase searchProductsUseCase;

        @Test
        @DisplayName("Deve pesquisar produtos públicos com filtro de categoria")
        void shouldSearchProductsWithCategory() {
            ProductPublicSearchController controller = new ProductPublicSearchController(searchProductsUseCase);
            Product product = createSampleProduct(1L, "Pão Francês", 1L, "Padaria");
            Page<Product> page = new PageImpl<>(List.of(product));

            when(searchProductsUseCase.execute(any(ProductFilter.class), any(PageRequestFilter.class))).thenReturn(page);

            ResponseEntity<Page<ProductPublicResponse>> response = controller.search("Pão", "Padaria", ProductStatus.ACTIVE, 0, 10);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getTotalElements()).isEqualTo(1);
        }

        @Test
        @DisplayName("Deve pesquisar produtos públicos sem filtro de categoria")
        void shouldSearchProductsWithoutCategory() {
            ProductPublicSearchController controller = new ProductPublicSearchController(searchProductsUseCase);
            Product product = createSampleProduct(1L, "Pão Francês", 1L, "Padaria");
            Page<Product> page = new PageImpl<>(List.of(product));

            when(searchProductsUseCase.execute(any(ProductFilter.class), any(PageRequestFilter.class))).thenReturn(page);

            ResponseEntity<Page<ProductPublicResponse>> response = controller.search("Pão", null, null, 0, 10);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
        }
    }

    @Nested
    @DisplayName("ProductAdminSearchController")
    class ProductAdminSearchControllerTests {
        @Mock
        private SearchProductsUseCase searchProductsUseCase;

        @Test
        @DisplayName("Deve pesquisar produtos no admin com categoryId e status")
        void shouldSearchAdminWithCategoryId() {
            ProductAdminSearchController controller = new ProductAdminSearchController(searchProductsUseCase);
            Product product = createSampleProduct(1L, "Pão Francês", 1L, "Padaria");
            Page<Product> page = new PageImpl<>(List.of(product));

            when(searchProductsUseCase.execute(any(ProductFilter.class), any(PageRequestFilter.class))).thenReturn(page);

            ResponseEntity<Page<ProductAdminResponse>> response = controller.search("Pão", 1L, ProductStatus.ACTIVE, 0, 10);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getContent().get(0).name()).isEqualTo("Pão Francês");
        }

        @Test
        @DisplayName("Deve pesquisar produtos no admin sem categoryId")
        void shouldSearchAdminWithoutCategoryId() {
            ProductAdminSearchController controller = new ProductAdminSearchController(searchProductsUseCase);
            Product product = createSampleProduct(1L, "Pão Francês", 1L, "Padaria");
            Page<Product> page = new PageImpl<>(List.of(product));

            when(searchProductsUseCase.execute(any(ProductFilter.class), any(PageRequestFilter.class))).thenReturn(page);

            ResponseEntity<Page<ProductAdminResponse>> response = controller.search(null, null, null, 0, 10);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
        }
    }

    @Nested
    @DisplayName("ProductCreateController")
    class ProductCreateControllerTests {
        @Mock
        private CreateProductUseCase createProductUseCase;

        @Test
        @DisplayName("Deve criar produto e retornar 201 Created")
        void shouldCreateProduct() {
            ProductCreateController controller = new ProductCreateController(createProductUseCase);
            MockMultipartFile file = new MockMultipartFile("photo", "teste.webp", "image/webp", new byte[]{1, 2, 3});
            CreateProductRequest request = new CreateProductRequest("Bolo", new BigDecimal("1.200"), file, 1L);

            Product product = createSampleProduct(2L, "Bolo", 1L, "Confeitaria");
            when(createProductUseCase.execute(request)).thenReturn(product);

            ResponseEntity<ProductAdminResponse> response = controller.create(request);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().name()).isEqualTo("Bolo");
        }
    }

    @Nested
    @DisplayName("ProductUpdateController")
    class ProductUpdateControllerTests {
        @Mock
        private UpdateProductUseCase updateProductUseCase;
        @Mock
        private WebpImageValidator webpImageValidator;

        @Test
        @DisplayName("Deve atualizar produto e validar imagem quando enviada")
        void shouldUpdateProductWithPhoto() {
            ProductUpdateController controller = new ProductUpdateController(updateProductUseCase, webpImageValidator);
            MockMultipartFile file = new MockMultipartFile("photo", "nova.webp", "image/webp", new byte[]{1, 2, 3});
            UpdateProductRequest request = new UpdateProductRequest("Bolo Recheado", new BigDecimal("1.500"), file, 1L, ProductStatus.ACTIVE, 1L);

            Product product = createSampleProduct(2L, "Bolo Recheado", 1L, "Confeitaria");
            when(updateProductUseCase.execute(eq(new ProductId(2L)), eq(request))).thenReturn(product);

            ResponseEntity<ProductAdminResponse> response = controller.update(2L, request);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            verify(webpImageValidator).validate(file);
        }

        @Test
        @DisplayName("Deve atualizar produto sem foto sem invocar validação de imagem")
        void shouldUpdateProductWithoutPhoto() {
            ProductUpdateController controller = new ProductUpdateController(updateProductUseCase, webpImageValidator);
            UpdateProductRequest request = new UpdateProductRequest("Bolo Simples", null, null, null, null, null);

            Product product = createSampleProduct(2L, "Bolo Simples", 1L, "Confeitaria");
            when(updateProductUseCase.execute(eq(new ProductId(2L)), eq(request))).thenReturn(product);

            ResponseEntity<ProductAdminResponse> response = controller.update(2L, request);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            verifyNoInteractions(webpImageValidator);
        }
    }

    @Nested
    @DisplayName("ProductDeleteController")
    class ProductDeleteControllerTests {
        @Mock
        private DeleteProductUseCase deleteProductUseCase;
        @Mock
        private BatchDeleteProductsUseCase batchDeleteProductsUseCase;

        @Test
        @DisplayName("Deve excluir produto por id e retornar 204")
        void shouldDeleteProductById() {
            ProductDeleteController controller = new ProductDeleteController(deleteProductUseCase, batchDeleteProductsUseCase);

            ResponseEntity<Void> response = controller.delete(5L);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
            verify(deleteProductUseCase).execute(new ProductId(5L));
        }

        @Test
        @DisplayName("Deve excluir produtos em lote e retornar 204")
        void shouldBatchDeleteProducts() {
            ProductDeleteController controller = new ProductDeleteController(deleteProductUseCase, batchDeleteProductsUseCase);
            BatchDeleteProductsRequest request = new BatchDeleteProductsRequest(List.of(1L, 2L));

            ResponseEntity<Void> response = controller.batchDelete(request);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
            verify(batchDeleteProductsUseCase).execute(request);
        }
    }

    @Nested
    @DisplayName("ProductReorderController")
    class ProductReorderControllerTests {
        @Mock
        private ReorderProductsUseCase reorderProductsUseCase;

        @Test
        @DisplayName("Deve reordenar produtos e retornar 204")
        void shouldReorderProducts() {
            ProductReorderController controller = new ProductReorderController(reorderProductsUseCase);
            ReorderProductsRequest request = new ReorderProductsRequest(List.of(3L, 1L, 2L));

            ResponseEntity<Void> response = controller.reorder(request);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
            verify(reorderProductsUseCase).execute(request);
        }
    }

    @Nested
    @DisplayName("ProductImageController")
    class ProductImageControllerTests {
        @Mock
        private LoadProductImageUseCase loadProductImageUseCase;

        @Test
        @DisplayName("Deve carregar e retornar imagem com headers corretos")
        void shouldLoadImageSuccessfully() {
            ProductImageController controller = new ProductImageController(loadProductImageUseCase);
            Resource resource = new ByteArrayResource(new byte[]{1, 2, 3, 4}) {
                @Override
                public String getFilename() {
                    return "produto-1.webp";
                }
            };
            when(loadProductImageUseCase.execute("produto-1.webp")).thenReturn(resource);

            ResponseEntity<Resource> response = controller.getImage("produto-1.webp");

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getHeaders().getContentType().toString()).isEqualTo("image/webp");
            assertThat(response.getHeaders().getFirst("X-Content-Type-Options")).isEqualTo("nosniff");
            assertThat(response.getHeaders().getFirst("Cache-Control")).contains("max-age=2592000"); // 30 dias
            assertThat(response.getBody()).isNotNull();
        }
    }
}
