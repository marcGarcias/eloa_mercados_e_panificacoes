package garcias.api.catalog.category.application.services.category;

import garcias.api.catalog.category.application.dto.requests.CreateCategoryRequest;
import garcias.api.catalog.category.domain.entities.Category;
import garcias.api.catalog.category.domain.persistence.CategoryRepository;
import garcias.api.catalog.category.domain.valueobjects.CategoryId;
import garcias.api.catalog.category.domain.valueobjects.CategoryName;
import garcias.api.shared.exceptions.ObjectAlreadyExistsException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreateCategoryService Unit Tests")
class CreateCategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CreateCategoryService createCategoryService;

    @Test
    @DisplayName("Deve criar categoria com sucesso quando o nome for único")
    void shouldCreateCategorySuccessfully() {
        CreateCategoryRequest request = new CreateCategoryRequest("Padaria");
        Category savedCategory = Category.create(new CategoryId(1L), new CategoryName("Padaria"));

        when(categoryRepository.existsByName(any(CategoryName.class))).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(savedCategory);

        Category result = createCategoryService.execute(request);

        assertThat(result).isNotNull();
        assertThat(result.getId().value()).isEqualTo(1L);
        assertThat(result.getName().value()).isEqualTo("Padaria");

        ArgumentCaptor<Category> captor = ArgumentCaptor.forClass(Category.class);
        verify(categoryRepository).save(captor.capture());
        assertThat(captor.getValue().getName().value()).isEqualTo("Padaria");
    }

    @Test
    @DisplayName("Deve lançar ObjectAlreadyExistsException quando nome de categoria já existir")
    void shouldThrowWhenCategoryNameAlreadyExists() {
        CreateCategoryRequest request = new CreateCategoryRequest("Bebidas");

        when(categoryRepository.existsByName(any(CategoryName.class))).thenReturn(true);

        assertThatThrownBy(() -> createCategoryService.execute(request))
                .isInstanceOf(ObjectAlreadyExistsException.class)
                .hasMessageContaining("Category name");

        verify(categoryRepository, never()).save(any());
    }
}
