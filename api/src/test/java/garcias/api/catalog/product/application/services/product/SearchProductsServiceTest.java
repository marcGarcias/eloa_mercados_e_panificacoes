package garcias.api.catalog.product.application.services.product;

import garcias.api.catalog.category.domain.valueobjects.CategoryId;
import garcias.api.catalog.product.application.dto.requests.PageRequestFilter;
import garcias.api.catalog.product.domain.entities.Product;
import garcias.api.catalog.product.domain.enums.ProductStatus;
import garcias.api.catalog.product.domain.repositories.ProductRepository;
import garcias.api.catalog.product.domain.valueobjects.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("SearchProductsService Unit Tests")
class SearchProductsServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private SearchProductsService searchProductsService;

    @Test
    @DisplayName("Deve buscar produtos passando filtros e ordenação por position ascendente")
    void shouldSearchProductsWithFiltersAndPositionSorting() {
        ProductFilter filter = new ProductFilter("Pão", new CategoryId(1L), null, ProductStatus.ACTIVE);
        PageRequestFilter pageRequest = new PageRequestFilter(0, 10);

        Product product = new Product(
                new ProductId(1L), new ProductName("Pão Francês"), new ProductWeight(new BigDecimal("0.050")),
                new CatalogPosition(1L), new CategoryId(1L), null, ProductStatus.ACTIVE, new ProductPhoto("pao.webp")
        );
        Page<Product> expectedPage = new PageImpl<>(List.of(product));

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        when(productRepository.search(eq(filter), pageableCaptor.capture())).thenReturn(expectedPage);

        Page<Product> result = searchProductsService.execute(filter, pageRequest);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName().value()).isEqualTo("Pão Francês");

        Pageable capturedPageable = pageableCaptor.getValue();
        assertThat(capturedPageable.getPageNumber()).isEqualTo(0);
        assertThat(capturedPageable.getPageSize()).isEqualTo(10);
        assertThat(capturedPageable.getSort().getOrderFor("position")).isNotNull();
    }
}
