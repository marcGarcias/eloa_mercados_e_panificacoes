package garcias.api.catalog.category.domain.exceptions;


import garcias.api.catalog.category.domain.valueobjects.CategoryId;
import garcias.api.shared.exceptions.ConflictException;


public class CategoryHasProductsException extends ConflictException {
    public CategoryHasProductsException(CategoryId id) {
        super("Cannot delete category with id " + id.value() + " because it contains products.");}
}