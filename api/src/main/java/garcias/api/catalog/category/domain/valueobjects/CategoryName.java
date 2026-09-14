package garcias.api.catalog.category.domain.valueobjects;

import garcias.api.shared.exceptions.AttributeCannotBeEmptyException;
import garcias.api.shared.exceptions.AttributeTooLongException;
import garcias.api.shared.exceptions.AttributeTooShortException;

import java.util.Objects;

public record CategoryName(String value) {

    public CategoryName {

        Objects.requireNonNull(value);

        if (value.isBlank()) {
            throw new AttributeCannotBeEmptyException("Category name");
        }

        if (value.length() < 2) {
            throw new AttributeTooShortException("Category name", "2");
        }

        if (value.length() > 16) {
            throw new AttributeTooLongException("Category name", "16");
        }
    }
}
