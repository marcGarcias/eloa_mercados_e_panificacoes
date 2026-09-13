package garcias.api.catalog.category.infrastructure.mapper;

import garcias.api.catalog.category.domain.entities.Category;
import garcias.api.catalog.category.domain.valueobjects.CategoryId;
import garcias.api.catalog.category.domain.valueobjects.CategoryName;
import garcias.api.catalog.category.infrastructure.persistence.CategoryJpaEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("CategoryMapper and CategoryJpaEntity Unit Tests")
class CategoryMapperTest {

    @Test
    @DisplayName("Deve mapear Category com ID vazio para CategoryJpaEntity com create()")
    void shouldMapCategoryWithEmptyIdToJpaEntity() {
        Category category = Category.create(CategoryId.empty(), new CategoryName("Bebidas"));

        CategoryJpaEntity entity = CategoryMapper.toEntity(category);

        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isNull();
        assertThat(entity.getName()).isEqualTo("Bebidas");
    }

    @Test
    @DisplayName("Deve mapear Category com ID existente para CategoryJpaEntity com withId()")
    void shouldMapCategoryWithExistingIdToJpaEntity() {
        Category category = Category.create(new CategoryId(15L), new CategoryName("Frios"));

        CategoryJpaEntity entity = CategoryMapper.toEntity(category);

        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(15L);
        assertThat(entity.getName()).isEqualTo("Frios");
    }

    @Test
    @DisplayName("Deve mapear CategoryJpaEntity para Domain Category com toDomain()")
    void shouldMapJpaEntityToDomainCategory() {
        CategoryJpaEntity entity = CategoryJpaEntity.withId(25L, "Confeitaria");

        Category domain = CategoryMapper.toDomain(entity);

        assertThat(domain).isNotNull();
        assertThat(domain.getId().value()).isEqualTo(25L);
        assertThat(domain.getName().value()).isEqualTo("Confeitaria");
    }
}
