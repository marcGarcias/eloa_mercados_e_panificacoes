package garcias.api.catalog.product.application.services.product;

import garcias.api.catalog.category.domain.valueobjects.CategoryId;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DeleteProductService Unit Tests")
class DeleteProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ImageStorage imageStorage;

    @InjectMocks
    private DeleteProductService deleteProductService;

    @BeforeEach
    void setUp() {
        TransactionSynchronizationManager.initSynchronization();
    }

    @AfterEach
    void tearDown() {
        TransactionSynchronizationManager.clearSynchronization();
    }

    @Test
    @DisplayName("Deve deletar produto com sucesso e remover imagem no afterCommit")
    void shouldDeleteProductSuccessfullyAndTriggerAfterCommit() {
        ProductId id = new ProductId(1L);
        CatalogPosition position = new CatalogPosition(3L);
        Product product = new Product(
                id,
                new ProductName("Pão Francês"),
                new ProductWeight(new BigDecimal("0.050")),
                position,
                new CategoryId(10L),
                null,
                ProductStatus.ACTIVE,
                new ProductPhoto("uploads/pao.webp")
        );

        when(productRepository.findById(id)).thenReturn(Optional.of(product));

        deleteProductService.execute(id);

        verify(productRepository).delete(product);
        verify(productRepository).reorganizePositionsAfterDelete(position);

        // Executa synchronizations para testar afterCommit
        for (TransactionSynchronization sync : TransactionSynchronizationManager.getSynchronizations()) {
            sync.afterCommit();
        }

        verify(imageStorage).delete("uploads/pao.webp");
    }

    @Test
    @DisplayName("Deve lançar ObjectNotFoundException quando produto não existir")
    void shouldThrowWhenProductNotFound() {
        ProductId id = new ProductId(99L);
        when(productRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> deleteProductService.execute(id))
                .isInstanceOf(ObjectNotFoundException.class);

        verify(productRepository, never()).delete(any());
        verify(productRepository, never()).reorganizePositionsAfterDelete(any());
    }
}
