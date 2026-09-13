package garcias.api.catalog.product.application.services.product;

import garcias.api.catalog.category.domain.valueobjects.CategoryId;
import garcias.api.catalog.product.application.dto.requests.BatchDeleteProductsRequest;
import garcias.api.catalog.product.application.storage.ImageStorage;
import garcias.api.catalog.product.domain.entities.Product;
import garcias.api.catalog.product.domain.enums.ProductStatus;
import garcias.api.catalog.product.domain.repositories.ProductRepository;
import garcias.api.catalog.product.domain.valueobjects.*;
import garcias.api.shared.exceptions.ObjectNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BatchDeleteProductsService Unit Tests")
class BatchDeleteProductsServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ImageStorage imageStorage;

    @InjectMocks
    private BatchDeleteProductsService batchDeleteProductsService;

    @BeforeEach
    void setUp() {
        TransactionSynchronizationManager.initSynchronization();
    }

    @AfterEach
    void tearDown() {
        TransactionSynchronizationManager.clearSynchronization();
    }

    @Test
    @DisplayName("Deve deletar produtos em lote com sucesso e remover imagens no afterCommit")
    void shouldDeleteBatchProductsSuccessfully() {
        ProductId id1 = new ProductId(1L);
        ProductId id2 = new ProductId(2L);

        Product p1 = new Product(
                id1, new ProductName("Pão"), new ProductWeight(new BigDecimal("0.050")),
                new CatalogPosition(1L), new CategoryId(10L), null, ProductStatus.ACTIVE,
                new ProductPhoto("uploads/pao.webp")
        );
        Product p2 = new Product(
                id2, new ProductName("Bolo"), new ProductWeight(new BigDecimal("1.000")),
                new CatalogPosition(2L), new CategoryId(10L), null, ProductStatus.ACTIVE,
                new ProductPhoto("uploads/bolo.webp")
        );

        BatchDeleteProductsRequest request = new BatchDeleteProductsRequest(List.of(1L, 2L));

        when(productRepository.findAllByIds(anyList())).thenReturn(List.of(p1, p2));

        batchDeleteProductsService.execute(request);

        verify(productRepository).delete(p1);
        verify(productRepository).delete(p2);
        verify(productRepository).reorganizeAllPositions();

        for (TransactionSynchronization sync : TransactionSynchronizationManager.getSynchronizations()) {
            sync.afterCommit();
        }

        verify(imageStorage).delete("uploads/pao.webp");
        verify(imageStorage).delete("uploads/bolo.webp");
    }

    @Test
    @DisplayName("Deve lançar ObjectNotFoundException se algum id não for encontrado na listagem")
    void shouldThrowWhenAnyIdNotFound() {
        ProductId id1 = new ProductId(1L);
        Product p1 = new Product(
                id1, new ProductName("Pão"), new ProductWeight(new BigDecimal("0.050")),
                new CatalogPosition(1L), new CategoryId(10L), null, ProductStatus.ACTIVE,
                new ProductPhoto("uploads/pao.webp")
        );

        BatchDeleteProductsRequest request = new BatchDeleteProductsRequest(List.of(1L, 99L));

        when(productRepository.findAllByIds(anyList())).thenReturn(List.of(p1));

        assertThatThrownBy(() -> batchDeleteProductsService.execute(request))
                .isInstanceOf(ObjectNotFoundException.class);

        verify(productRepository, never()).delete(any());
        verify(productRepository, never()).reorganizeAllPositions();
    }
}
