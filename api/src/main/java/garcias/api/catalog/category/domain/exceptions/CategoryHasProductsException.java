package garcias.api.catalog.category.domain.exceptions;

import garcias.api.catalog.category.domain.valueobjects.CategoryId;
import garcias.api.shared.exceptions.ConflictException;

public class CategoryHasProductsException extends ConflictException {
    public CategoryHasProductsException(CategoryId id) {
        super("Não é possível excluir esta categoria porque existem produtos vinculados a ela. Exclua ou mova os produtos antes de remover a categoria.");
    }

    public CategoryHasProductsException() {
        super("Não é possível excluir esta categoria porque existem produtos vinculados a ela. Exclua ou mova os produtos antes de remover a categoria.");
    }
}
