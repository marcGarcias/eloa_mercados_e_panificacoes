package garcias.api.catalog.product.infrastructure.presentation.admin.product;


import garcias.api.catalog.product.application.usecases.product.DeleteProductUseCase;
import garcias.api.catalog.product.domain.valueobjects.ProductId;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/admin/products")
public class ProductDeleteController {


    private final DeleteProductUseCase deleteProductUseCase;


    public ProductDeleteController(
            DeleteProductUseCase deleteProductUseCase
    ) {
        this.deleteProductUseCase = deleteProductUseCase;
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
}