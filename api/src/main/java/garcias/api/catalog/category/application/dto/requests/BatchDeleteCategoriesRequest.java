package garcias.api.catalog.category.application.dto.requests;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record BatchDeleteCategoriesRequest(
        @NotNull(message = "Category list is required")
        @NotEmpty(message = "Category list must not be empty")
        List<@NotNull Long> ids
) {}
