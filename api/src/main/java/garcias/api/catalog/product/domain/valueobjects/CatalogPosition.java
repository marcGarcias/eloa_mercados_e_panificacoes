package garcias.api.catalog.product.domain.valueobjects;

import garcias.api.shared.exceptions.AttributeMustBeGreaterThanZeroException;
import garcias.api.shared.exceptions.ValueObjectCannotBeNullException;

public record CatalogPosition(Long value) {

    public CatalogPosition {
        if (value == null) {
            throw new ValueObjectCannotBeNullException("Catalog position");
        }

        if (value < 0) {
            throw new AttributeMustBeGreaterThanZeroException("Catalog position", value);
        }
    }

    public CatalogPosition next() {
        return new CatalogPosition(value + 1);
    }

    public static CatalogPosition first() {
        return new CatalogPosition(1L);
    }
}