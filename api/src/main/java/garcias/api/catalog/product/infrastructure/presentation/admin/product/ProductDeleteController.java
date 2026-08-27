package garcias.api.catalog.product.infrastructure.presentation.admin.product;


import garcias.api.catalog.product.application.dto.requests.BatchDeleteProductsRequest;
import garcias.api.catalog.product.application.usecases.product.BatchDeleteProductsUseCase;
import garcias.api.catalog.product.application.usecases.product.DeleteProductUseCase;
import garcias.api.catalog.product.domain.valueobjects.ProductId;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/admin/products")
public class ProductDeleteController {


    private final DeleteProductUseCase deleteProductUseCase;
    private final BatchDeleteProductsUseCase batchDeleteProductsUseCase;


    public ProductDeleteController(
            DeleteProductUseCase deleteProductUseCase,
            BatchDeleteProductsUseCase batchDeleteProductsUseCase
    ) {
        this.deleteProductUseCase = deleteProductUseCase;
        this.batchDeleteProductsUseCase = batchDeleteProductsUseCase;
    }


    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete product",
            description = """
                    Removes a product by its identifier.
                    """
    )
    @ApiResponse(
            responseCode = "204",
            description = "Product deleted successfully"
    )
    @ApiResponse(
            responseCode = "404",
            description = "Product not found"
    )
    public ResponseEntity<Void> delete(

            @PathVariable Long id

    ) {


        deleteProductUseCase.execute(
                new ProductId(id)
        );


        return ResponseEntity.noContent().build();
    }

    @PostMapping("/batch-delete")
    @Operation(
            summary = "Delete products in batch",
            description = "Removes multiple products by their identifiers in a single atomic transaction."
    )
    @ApiResponse(
            responseCode = "204",
            description = "Products deleted successfully"
    )
    @ApiResponse(
            responseCode = "400",
            description = "Invalid request or empty product ID list"
    )
    public ResponseEntity<Void> batchDelete(
            @Valid
            @RequestBody
            BatchDeleteProductsRequest request
    ) {
        batchDeleteProductsUseCase.execute(request);
        return ResponseEntity.noContent().build();
    }
}