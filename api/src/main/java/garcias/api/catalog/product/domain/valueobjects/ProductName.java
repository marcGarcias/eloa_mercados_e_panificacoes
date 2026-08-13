package garcias.api.catalog.product.domain.valueobjects;

import garcias.api.shared.exceptions.AttributeCannotBeEmptyException;
import garcias.api.shared.exceptions.AttributeTooLongException;
import garcias.api.shared.exceptions.ValueObjectCannotBeNullException;

public record ProductName(String value) {

    public ProductName {

        if (value == null) {
            throw new ValueObjectCannotBeNullException("Product name");
        }

        if (value.isBlank()) {
            throw new AttributeCannotBeEmptyException("Product name");
        }

        if (value.length() > 120) {
            throw new AttributeTooLongException("Product name", "120");
        }
    }

}