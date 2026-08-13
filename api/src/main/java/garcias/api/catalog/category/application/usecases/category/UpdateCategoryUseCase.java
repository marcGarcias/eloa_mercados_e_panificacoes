package garcias.api.catalog.category.application.usecases.category;

import garcias.api.catalog.category.application.dto.requests.UpdateCategoryRequest;
import garcias.api.catalog.category.domain.entities.Category;
import garcias.api.catalog.category.domain.valueobjects.CategoryId;

public interface UpdateCategoryUseCase {
    Category execute(
            CategoryId id,
            UpdateCategoryRequest request
    );

}