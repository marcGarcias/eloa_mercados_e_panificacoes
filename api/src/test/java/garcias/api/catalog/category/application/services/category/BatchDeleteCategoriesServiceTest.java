package garcias.api.catalog.category.application.services.category;

import garcias.api.catalog.category.application.dto.requests.BatchDeleteCategoriesRequest;
import garcias.api.catalog.category.domain.entities.Category;
import garcias.api.catalog.category.domain.exceptions.CategoryHasProductsException;
import garcias.api.catalog.category.domain.persistence.CategoryRepository;
import garcias.api.catalog.category.domain.valueobjects.CategoryId;
import garcias.api.catalog.category.domain.valueobjects.CategoryName;
import garcias.api.catalog.product.domain.repositories.ProductRepository;
import garcias.api.shared.exceptions.ObjectNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BatchDeleteCategoriesService Unit Tests")
class BatchDeleteCategoriesServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private BatchDeleteCategoriesService batchDeleteCategoriesService;

    @Test
    @DisplayName("Deve deletar categorias em lote com sucesso quando nenhuma possuir produtos")
    void shouldDeleteBatchCategoriesSuccessfully() {
        CategoryId id1 = new CategoryId(1L);
        CategoryId id2 = new CategoryId(2L);
        Category cat1 = Category.create(id1, new CategoryName("Cat 1"));
        Category cat2 = Category.create(id2, new CategoryName("Cat 2"));

        BatchDeleteCategoriesRequest request = new BatchDeleteCategoriesRequest(List.of(1L, 2L));

        when(categoryRepository.findById(id1)).thenReturn(Optional.of(cat1));
        when(categoryRepository.findById(id2)).thenReturn(Optional.of(cat2));
        when(productRepository.existsByCategoryId(id1)).thenReturn(false);
        when(productRepository.existsByCategoryId(id2)).thenReturn(false);

        batchDeleteCategoriesService.execute(request);

        verify(categoryRepository).delete(cat1);
        verify(categoryRepository).delete(cat2);
    }

    @Test
    @DisplayName("Deve lançar ObjectNotFoundException se alguma categoria do lote não existir")
    void shouldThrowWhenAnyCategoryInBatchNotFound() {
        CategoryId id1 = new CategoryId(1L);
        BatchDeleteCategoriesRequest request = new BatchDeleteCategoriesRequest(List.of(1L, 99L));

        when(categoryRepository.findById(id1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> batchDeleteCategoriesService.execute(request))
                .isInstanceOf(ObjectNotFoundException.class);

        verify(categoryRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Deve lançar CategoryHasProductsException se alguma categoria do lote possuir produtos")
    void shouldThrowWhenAnyCategoryInBatchHasProducts() {
        CategoryId id1 = new CategoryId(1L);
        Category cat1 = Category.create(id1, new CategoryName("Cat 1"));
        BatchDeleteCategoriesRequest request = new BatchDeleteCategoriesRequest(List.of(1L));

        when(categoryRepository.findById(id1)).thenReturn(Optional.of(cat1));
        when(productRepository.existsByCategoryId(id1)).thenReturn(true);

        assertThatThrownBy(() -> batchDeleteCategoriesService.execute(request))
                .isInstanceOf(CategoryHasProductsException.class);

        verify(categoryRepository, never()).delete(any());
    }
}
