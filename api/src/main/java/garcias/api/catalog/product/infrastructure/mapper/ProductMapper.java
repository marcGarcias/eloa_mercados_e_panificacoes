package garcias.api.catalog.product.infrastructure.mapper;

import garcias.api.catalog.category.domain.valueobjects.CategoryId;
import garcias.api.catalog.category.domain.valueobjects.CategoryName;
import garcias.api.catalog.category.infrastructure.persistence.CategoryJpaEntity;
import garcias.api.catalog.product.domain.entities.Product;
import garcias.api.catalog.product.domain.valueobjects.*;
import garcias.api.catalog.product.infrastructure.persistence.ProductJpaEntity;

public final class ProductMapper {

    private ProductMapper() {
    }

    public static Product toDomain(ProductJpaEntity entity) {

        return new Product(
                new ProductId(entity.getId()),
                new ProductName(entity.getName()),
                new ProductWeight(entity.getWeight()),
                new CatalogPosition(entity.getPosition()),
                new CategoryId(entity.getCategory().getId()),
                new CategoryName(entity.getCategory().getName()),
                entity.getStatus(),
                new ProductPhoto(entity.getPhoto())
        );
    }

    public static ProductJpaEntity toEntity(Product product) {

        CategoryJpaEntity category =
                CategoryJpaEntity.reference(
                        product.getCategoryId().value()
                );

        if (product.getId().isEmpty()) {

            return ProductJpaEntity.create(
                    product.getName().value(),
                    product.getWeight().value(),
                    product.getPosition().value(),
                    product.getPhoto().value(),
                    category,
                    product.getStatus()
            );
        }

        return ProductJpaEntity.withId(
                product.getId().value(),
                product.getName().value(),
                product.getWeight().value(),
                product.getPosition().value(),
                product.getPhoto().value(),
                category,
                product.getStatus()
        );
    }
}