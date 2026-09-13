package garcias.api.catalog.category.application.services.category;

import garcias.api.catalog.category.domain.entities.Category;
import garcias.api.catalog.category.domain.persistence.CategoryRepository;
import garcias.api.catalog.category.domain.valueobjects.CategoryId;
import garcias.api.catalog.category.domain.valueobjects.CategoryName;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("SearchCategoriesService Unit Tests")
class SearchCategoriesServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private SearchCategoriesService searchCategoriesService;

    @Test
    @DisplayName("Deve listar todas as categorias sem paginação")
    void shouldFindAllCategoriesWithoutPagination() {
        Category cat1 = Category.create(new CategoryId(1L), new CategoryName("Cat 1"));
        Category cat2 = Category.create(new CategoryId(2L), new CategoryName("Cat 2"));
        List<Category> categories = List.of(cat1, cat2);

        when(categoryRepository.findAll()).thenReturn(categories);

        List<Category> result = searchCategoriesService.execute();

        assertThat(result).hasSize(2).containsExactly(cat1, cat2);
        verify(categoryRepository).findAll();
    }

    @Test
    @DisplayName("Deve buscar categorias de forma paginada com filtro de nome")
    void shouldFindAllCategoriesWithPaginationAndFilter() {
        Category cat1 = Category.create(new CategoryId(1L), new CategoryName("Padaria"));
        Pageable pageable = PageRequest.of(0, 10);
        Page<Category> page = new PageImpl<>(List.of(cat1), pageable, 1);

        when(categoryRepository.findAll("Pad", pageable)).thenReturn(page);

        Page<Category> result = searchCategoriesService.execute("Pad", pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName().value()).isEqualTo("Padaria");
        verify(categoryRepository).findAll("Pad", pageable);
    }
}
