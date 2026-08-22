package garcias.api.catalog.product.application.dto.requests;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ReorderProductsRequest(

        @NotNull(message = "Order list is required")
        @NotEmpty(message = "Order list must not be empty")
        List<@NotNull Long> order

) {}
