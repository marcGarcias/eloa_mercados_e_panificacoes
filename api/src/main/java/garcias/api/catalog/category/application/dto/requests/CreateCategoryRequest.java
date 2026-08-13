package garcias.api.catalog.category.application.dto.requests;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


public record CreateCategoryRequest(

        @NotBlank(message = "Category name is required")
        @Size(
                min = 2,
                max = 50,
                message = "Category name must contain between 2 and 50 characters"
        )
        String name

) {
}