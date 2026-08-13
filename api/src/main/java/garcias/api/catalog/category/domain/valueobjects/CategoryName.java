package garcias.api.catalog.category.domain.valueobjects;

import garcias.api.shared.exceptions.AttributeCannotBeEmptyException;
import garcias.api.shared.exceptions.AttributeTooLongException;

import java.util.Objects;

public record CategoryName(String value) {

    public CategoryName {

        Objects.requireNonNull(value);

        if (value.isBlank()) {
            throw new AttributeCannotBeEmptyException("Category name");
        }

        if (value.length() > 50) {
            throw new AttributeTooLongException("Category name", "50");
        }
    }
}