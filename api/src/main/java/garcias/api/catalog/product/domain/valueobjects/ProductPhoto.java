package garcias.api.catalog.product.domain.valueobjects;

import garcias.api.shared.exceptions.AttributeCannotBeEmptyException;
import garcias.api.shared.exceptions.ValueObjectCannotBeNullException;

public record ProductPhoto(String value) {

    public ProductPhoto {

        if (value == null) {
            throw new ValueObjectCannotBeNullException("ProductPhoto");
        }

        if (value.isBlank()) {
            throw new AttributeCannotBeEmptyException("ProductPhoto");
        }
    }

}