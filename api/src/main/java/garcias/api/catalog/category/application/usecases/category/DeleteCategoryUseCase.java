package garcias.api.catalog.category.application.usecases.category;

import garcias.api.catalog.category.domain.valueobjects.CategoryId;

public interface DeleteCategoryUseCase {

    void execute(CategoryId id);

}