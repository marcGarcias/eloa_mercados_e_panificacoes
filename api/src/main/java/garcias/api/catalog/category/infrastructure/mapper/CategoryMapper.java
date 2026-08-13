package garcias.api.catalog.category.infrastructure.mapper;

import garcias.api.catalog.category.domain.entities.Category;
import garcias.api.catalog.category.domain.valueobjects.CategoryId;
import garcias.api.catalog.category.domain.valueobjects.CategoryName;
import garcias.api.catalog.category.infrastructure.persistence.CategoryJpaEntity;

public final class CategoryMapper {

    private CategoryMapper() {
    }

    public static Category toDomain(CategoryJpaEntity entity) {

        return new Category(
                new CategoryId(entity.getId()),
                new CategoryName(entity.getName())
        );
    }

    public static CategoryJpaEntity toEntity(Category category) {

        if (category.getId().isEmpty()) {

            return CategoryJpaEntity.create(
                    category.getName().value()
            );
        }

        return CategoryJpaEntity.withId(
                category.getId().value(),
                category.getName().value()
        );
    }

}