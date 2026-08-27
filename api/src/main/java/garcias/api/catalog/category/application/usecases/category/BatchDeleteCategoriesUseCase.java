package garcias.api.catalog.category.application.usecases.category;

import garcias.api.catalog.category.application.dto.requests.BatchDeleteCategoriesRequest;

public interface BatchDeleteCategoriesUseCase {

    void execute(BatchDeleteCategoriesRequest request);

}
