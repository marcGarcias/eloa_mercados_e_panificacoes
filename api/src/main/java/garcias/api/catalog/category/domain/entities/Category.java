package garcias.api.catalog.category.domain.entities;

import garcias.api.catalog.category.domain.valueobjects.CategoryId;
import garcias.api.catalog.category.domain.valueobjects.CategoryName;

import java.util.Objects;

public class Category {

    private final CategoryId id;
    private CategoryName name;

    public CategoryId getId() {
        return id;
    }

    public CategoryName getName() {
        return name;
    }

    public Category(
            CategoryId id,
            CategoryName name
    ) {
        this.id = Objects.requireNonNull(id);
        this.name = Objects.requireNonNull(name);
    }

    public static Category create(
            CategoryId id,
            CategoryName name
    ) {
        return new Category(
                id,
                name
        );
    }

    public void rename(CategoryName newName) {
        this.name = Objects.requireNonNull(newName);
    }
}