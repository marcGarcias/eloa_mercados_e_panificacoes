package garcias.api.catalog.product.application.dto.requests;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record BatchDeleteProductsRequest(
        @NotNull(message = "Product list is required")
        @NotEmpty(message = "Product list must not be empty")
        List<@NotNull Long> ids
) {}
