package garcias.api.catalog.product.domain.valueobjects;

import garcias.api.catalog.category.domain.valueobjects.CategoryId;
import garcias.api.catalog.category.domain.valueobjects.CategoryName;
import garcias.api.catalog.product.domain.enums.ProductStatus;

public record ProductFilter(
        String name,
        CategoryId categoryId,
        CategoryName categoryName,
        ProductStatus status
) {

    public boolean isEmpty() {

        return (name == null || name.isBlank())
                && categoryId == null
                && categoryName == null
                && status == null;
    }
}