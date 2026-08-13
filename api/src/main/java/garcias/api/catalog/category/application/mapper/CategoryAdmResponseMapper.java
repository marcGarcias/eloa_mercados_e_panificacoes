package garcias.api.catalog.category.application.mapper;


import garcias.api.catalog.category.application.dto.responses.CategoryAdmResponse;
import garcias.api.catalog.category.domain.entities.Category;


public final class CategoryAdmResponseMapper {


    private CategoryAdmResponseMapper() {
    }


    public static CategoryAdmResponse toResponse(
            Category category
    ) {

        return new CategoryAdmResponse(
                category.getId().value(),
                category.getName().value()
        );
    }
}