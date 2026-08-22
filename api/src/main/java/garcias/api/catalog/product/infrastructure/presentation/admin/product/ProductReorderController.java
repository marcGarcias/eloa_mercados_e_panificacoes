package garcias.api.catalog.product.infrastructure.presentation.admin.product;

import garcias.api.catalog.product.application.dto.requests.ReorderProductsRequest;
import garcias.api.catalog.product.application.usecases.product.ReorderProductsUseCase;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/admin/products")
public class ProductReorderController {


    private final ReorderProductsUseCase reorderProductsUseCase;


    public ProductReorderController(ReorderProductsUseCase reorderProductsUseCase) {
        this.reorderProductsUseCase = reorderProductsUseCase;
    }


    @PutMapping("/reorder")
    @Operation(
            summary = "Reorder products",
            description = "Reorders all products in a single atomic operation. "
                    + "The list must contain all product IDs in the desired display order. "
                    + "Position is determined by the index in the list (1-based)."
    )
    @ApiResponse(
            responseCode = "204",
            description = "Products reordered successfully"
    )
    @ApiResponse(
            responseCode = "400",
            description = "Invalid request or empty order list"
    )
    @ApiResponse(
            responseCode = "404",
            description = "One or more product IDs not found"
    )
    public ResponseEntity<Void> reorder(

            @Valid
            @RequestBody
            ReorderProductsRequest request

    ) {

        reorderProductsUseCase.execute(request);

        return ResponseEntity.noContent().build();
    }
}
