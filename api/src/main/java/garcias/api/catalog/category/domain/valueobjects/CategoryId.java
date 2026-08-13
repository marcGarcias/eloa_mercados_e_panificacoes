package garcias.api.catalog.category.domain.valueobjects;

import garcias.api.shared.exceptions.ValueObjectCannotBeNullException;

public record CategoryId(Long value) {

    public CategoryId {

        if (value == null) {
            throw new ValueObjectCannotBeNullException("CategoryId");
        }
    }

    public static CategoryId empty() {
        return new CategoryId(0L);
    }

    public boolean isEmpty() {
        return value == 0L;
    }
}