package garcias.api.catalog.category.application.services.category;

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

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DeleteCategoryService Unit Tests")
class DeleteCategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private DeleteCategoryService deleteCategoryService;

    @Test
    @DisplayName("Deve deletar categoria com sucesso quando não houver produtos associados")
    void shouldDeleteCategorySuccessfullyWhenNoProductsAssociated() {
        CategoryId id = new CategoryId(1L);
        Category category = Category.create(id, new CategoryName("Doces"));

        when(categoryRepository.findById(id)).thenReturn(Optional.of(category));
        when(productRepository.existsByCategoryId(id)).thenReturn(false);

        deleteCategoryService.execute(id);

        verify(categoryRepository).delete(category);
    }

    @Test
    @DisplayName("Deve lançar ObjectNotFoundException quando categoria não existir")
    void shouldThrowWhenCategoryNotFound() {
        CategoryId id = new CategoryId(99L);

        when(categoryRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> deleteCategoryService.execute(id))
                .isInstanceOf(ObjectNotFoundException.class);

        verify(categoryRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Deve lançar CategoryHasProductsException quando categoria possuir produtos vinculados")
    void shouldThrowWhenCategoryHasProductsAssociated() {
        CategoryId id = new CategoryId(1L);
        Category category = Category.create(id, new CategoryName("Doces"));

        when(categoryRepository.findById(id)).thenReturn(Optional.of(category));
        when(productRepository.existsByCategoryId(id)).thenReturn(true);

        assertThatThrownBy(() -> deleteCategoryService.execute(id))
                .isInstanceOf(CategoryHasProductsException.class);

        verify(categoryRepository, never()).delete(any());
    }
}
