package garcias.api.catalog.category.application.dto.requests;

import jakarta.validation.constraints.NotBlank;

public record UpdateCategoryRequest(
        @NotBlank
        String name
){}