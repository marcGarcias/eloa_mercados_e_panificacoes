package garcias.api.catalog.category.application.services.category;

import garcias.api.catalog.category.application.dto.requests.UpdateCategoryRequest;
import garcias.api.catalog.category.domain.entities.Category;
import garcias.api.catalog.category.domain.persistence.CategoryRepository;
import garcias.api.catalog.category.domain.valueobjects.CategoryId;
import garcias.api.catalog.category.domain.valueobjects.CategoryName;
import garcias.api.shared.exceptions.ObjectAlreadyExistsException;
import garcias.api.shared.exceptions.ObjectNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UpdateCategoryService Unit Tests")
class UpdateCategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private UpdateCategoryService updateCategoryService;

    @Test
    @DisplayName("Deve lançar ObjectNotFoundException quando categoria não existir")
    void shouldThrowWhenCategoryNotFound() {
        CategoryId id = new CategoryId(99L);
        UpdateCategoryRequest request = new UpdateCategoryRequest("Novo Nome");

        when(categoryRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> updateCategoryService.execute(id, request))
                .isInstanceOf(ObjectNotFoundException.class);

        verify(categoryRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve manter nome atual quando request.name for nulo")
    void shouldKeepCurrentNameWhenRequestNameIsNull() {
        CategoryId id = new CategoryId(1L);
        Category category = Category.create(id, new CategoryName("Original"));
        UpdateCategoryRequest request = new UpdateCategoryRequest(null);

        when(categoryRepository.findById(id)).thenReturn(Optional.of(category));
        when(categoryRepository.save(category)).thenReturn(category);

        Category result = updateCategoryService.execute(id, request);

        assertThat(result.getName().value()).isEqualTo("Original");
        verify(categoryRepository).save(category);
    }

    @Test
    @DisplayName("Deve lançar ObjectAlreadyExistsException quando novo nome pertencer a outra categoria")
    void shouldThrowWhenNewNameBelongsToAnotherCategory() {
        CategoryId currentId = new CategoryId(1L);
        CategoryId otherId = new CategoryId(2L);
        Category currentCategory = Category.create(currentId, new CategoryName("Categoria A"));
        Category otherCategory = Category.create(otherId, new CategoryName("Categoria B"));

        UpdateCategoryRequest request = new UpdateCategoryRequest("Categoria B");

        when(categoryRepository.findById(currentId)).thenReturn(Optional.of(currentCategory));
        when(categoryRepository.findByName(new CategoryName("Categoria B"))).thenReturn(Optional.of(otherCategory));

        assertThatThrownBy(() -> updateCategoryService.execute(currentId, request))
                .isInstanceOf(ObjectAlreadyExistsException.class);

        verify(categoryRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve atualizar nome com sucesso quando novo nome for da própria categoria")
    void shouldUpdateSuccessfullyWhenNameBelongsToSameCategory() {
        CategoryId currentId = new CategoryId(1L);
        Category currentCategory = Category.create(currentId, new CategoryName("Categoria A"));
        UpdateCategoryRequest request = new UpdateCategoryRequest("Categoria A");

        when(categoryRepository.findById(currentId)).thenReturn(Optional.of(currentCategory));
        when(categoryRepository.findByName(new CategoryName("Categoria A"))).thenReturn(Optional.of(currentCategory));
        when(categoryRepository.save(currentCategory)).thenReturn(currentCategory);

        Category result = updateCategoryService.execute(currentId, request);

        assertThat(result.getName().value()).isEqualTo("Categoria A");
        verify(categoryRepository).save(currentCategory);
    }

    @Test
    @DisplayName("Deve atualizar nome com sucesso quando novo nome não existir no sistema")
    void shouldUpdateSuccessfullyWhenNewNameDoesNotExist() {
        CategoryId currentId = new CategoryId(1L);
        Category currentCategory = Category.create(currentId, new CategoryName("Antigo Nome"));
        UpdateCategoryRequest request = new UpdateCategoryRequest("Novo Nome");

        when(categoryRepository.findById(currentId)).thenReturn(Optional.of(currentCategory));
        when(categoryRepository.findByName(new CategoryName("Novo Nome"))).thenReturn(Optional.empty());
        when(categoryRepository.save(currentCategory)).thenReturn(currentCategory);

        Category result = updateCategoryService.execute(currentId, request);

        assertThat(result.getName().value()).isEqualTo("Novo Nome");
        verify(categoryRepository).save(currentCategory);
    }
}
