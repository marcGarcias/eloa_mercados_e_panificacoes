package garcias.api.catalog.category.application.mapper;


import garcias.api.catalog.category.application.dto.responses.CategoryWebResponse;
import garcias.api.catalog.category.domain.entities.Category;


public final class CategoryWebResponseMapper {


    private CategoryWebResponseMapper() {
    }


    public static CategoryWebResponse toResponse(
            Category category
    ) {

        return new CategoryWebResponse(
                category.getName().value()
        );
    }
}