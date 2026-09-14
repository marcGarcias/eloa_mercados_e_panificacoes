package garcias.api.catalog.category.application.dto.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateCategoryRequest(
        @NotBlank(message = "Category name is required")
        @Size(
                min = 2,
                max = 16,
                message = "Category name must contain between 2 and 16 characters"
        )
        String name
){}
