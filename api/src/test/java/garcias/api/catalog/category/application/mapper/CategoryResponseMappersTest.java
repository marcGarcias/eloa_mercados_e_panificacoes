package garcias.api.catalog.category.application.mapper;

import garcias.api.catalog.category.application.dto.responses.CategoryAdmResponse;
import garcias.api.catalog.category.application.dto.responses.CategoryWebResponse;
import garcias.api.catalog.category.domain.entities.Category;
import garcias.api.catalog.category.domain.valueobjects.CategoryId;
import garcias.api.catalog.category.domain.valueobjects.CategoryName;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Category Response Mappers Unit Tests")
class CategoryResponseMappersTest {

    @Test
    @DisplayName("Deve mapear Category para CategoryAdmResponse")
    void shouldMapToCategoryAdmResponse() {
        Category category = Category.create(new CategoryId(1L), new CategoryName("Padaria"));

        CategoryAdmResponse response = CategoryAdmResponseMapper.toResponse(category);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.name()).isEqualTo("Padaria");
    }

    @Test
    @DisplayName("Deve mapear Category para CategoryWebResponse")
    void shouldMapToCategoryWebResponse() {
        Category category = Category.create(new CategoryId(2L), new CategoryName("Confeitaria"));

        CategoryWebResponse response = CategoryWebResponseMapper.toResponse(category);

        assertThat(response.name()).isEqualTo("Confeitaria");
    }
}
