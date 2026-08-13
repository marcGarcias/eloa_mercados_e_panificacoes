package garcias.api.catalog.product.infrastructure.persistence.specification;

import garcias.api.catalog.category.domain.valueobjects.CategoryId;
import garcias.api.catalog.category.domain.valueobjects.CategoryName;
import garcias.api.catalog.product.domain.enums.ProductStatus;
import garcias.api.catalog.product.domain.valueobjects.ProductFilter;
import garcias.api.catalog.product.infrastructure.persistence.ProductJpaEntity;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

public final class ProductSpecification {

    private ProductSpecification() {
    }

    public static Specification<ProductJpaEntity> from(
            ProductFilter filter
    ) {

        if (filter == null || filter.isEmpty()) {
            return (root, query, cb) -> cb.conjunction();
        }

        return Specification.allOf(
                nameContains(filter.name()),
                categoryIdEquals(filter.categoryId()),
                categoryNameEquals(filter.categoryName()),
                statusEquals(filter.status())
        );
    }


    private static Specification<ProductJpaEntity> nameContains(
            String name
    ) {

        return (root, query, cb) -> {

            if (name == null || name.isBlank()) {
                return cb.conjunction();
            }

            return cb.like(
                    cb.lower(root.get("name")),
                    "%" + name.trim().toLowerCase() + "%"
            );
        };
    }


    private static Specification<ProductJpaEntity> categoryIdEquals(
            CategoryId categoryId
    ) {

        return (root, query, cb) -> {

            if (categoryId == null) {
                return cb.conjunction();
            }

            Join<Object, Object> category =
                    root.join("category");

            return cb.equal(
                    category.get("id"),
                    categoryId.value()
            );
        };
    }

    private static Specification<ProductJpaEntity> categoryNameEquals(
            CategoryName categoryName
    ) {

        return (root, query, cb) -> {

            if (categoryName == null) {
                return cb.conjunction();
            }

            Join<Object, Object> category =
                    root.join("category");

            return cb.equal(
                    cb.lower(category.get("name")),
                    categoryName.value().toLowerCase()
            );
        };
    }


    private static Specification<ProductJpaEntity> statusEquals(
            ProductStatus status
    ) {

        return (root, query, cb) -> {

            if (status == null) {
                return cb.conjunction();
            }

            return cb.equal(
                    root.get("status"),
                    status
            );
        };
    }
}