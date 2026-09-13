package garcias.api.catalog.product.application.services.product;

import garcias.api.catalog.category.domain.valueobjects.CategoryId;
import garcias.api.catalog.product.application.dto.requests.ReorderProductsRequest;
import garcias.api.catalog.product.domain.entities.Product;
import garcias.api.catalog.product.domain.enums.ProductStatus;
import garcias.api.catalog.product.domain.repositories.ProductRepository;
import garcias.api.catalog.product.domain.valueobjects.*;
import garcias.api.shared.exceptions.ObjectNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReorderProductsService Unit Tests")
class ReorderProductsServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ReorderProductsService reorderProductsService;

    @Test
    @DisplayName("Deve reordenar produtos atribuindo novas posições sequenciais")
    void shouldReorderProductsSuccessfully() {
        ProductId id1 = new ProductId(10L);
        ProductId id2 = new ProductId(20L);

        Product p1 = new Product(
                id1, new ProductName("Pão"), new ProductWeight(new BigDecimal("0.050")),
                new CatalogPosition(1L), new CategoryId(1L), null, ProductStatus.ACTIVE,
                new ProductPhoto("pao.webp")
        );
        Product p2 = new Product(
                id2, new ProductName("Bolo"), new ProductWeight(new BigDecimal("1.000")),
                new CatalogPosition(2L), new CategoryId(1L), null, ProductStatus.ACTIVE,
                new ProductPhoto("bolo.webp")
        );

        // Ordem invertida: 20L primeiro, 10L depois
        ReorderProductsRequest request = new ReorderProductsRequest(List.of(20L, 10L));

        when(productRepository.findAllByIds(anyList())).thenReturn(List.of(p1, p2));

        reorderProductsService.execute(request);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<ProductId, CatalogPosition>> captor = ArgumentCaptor.forClass(Map.class);
        verify(productRepository).reorderAll(captor.capture());

        Map<ProductId, CatalogPosition> resultPositions = captor.getValue();
        assertThat(resultPositions.get(id2).value()).isEqualTo(1L);
        assertThat(resultPositions.get(id1).value()).isEqualTo(2L);
    }

    @Test
    @DisplayName("Deve lançar ObjectNotFoundException com o ID ausente quando algum produto não for encontrado")
    void shouldThrowWhenAnyProductNotFound() {
        ProductId id1 = new ProductId(10L);
        Product p1 = new Product(
                id1, new ProductName("Pão"), new ProductWeight(new BigDecimal("0.050")),
                new CatalogPosition(1L), new CategoryId(1L), null, ProductStatus.ACTIVE,
                new ProductPhoto("pao.webp")
        );

        ReorderProductsRequest request = new ReorderProductsRequest(List.of(10L, 999L));

        when(productRepository.findAllByIds(anyList())).thenReturn(List.of(p1));

        assertThatThrownBy(() -> reorderProductsService.execute(request))
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessageContaining("999");

        verify(productRepository, never()).reorderAll(any());
    }
}
