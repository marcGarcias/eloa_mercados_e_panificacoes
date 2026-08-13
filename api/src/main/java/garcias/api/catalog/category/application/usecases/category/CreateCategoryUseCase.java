package garcias.api.catalog.category.application.usecases.category;

import garcias.api.catalog.category.application.dto.requests.CreateCategoryRequest;
import garcias.api.catalog.category.domain.entities.Category;

public interface CreateCategoryUseCase {

    Category execute(CreateCategoryRequest request);

}