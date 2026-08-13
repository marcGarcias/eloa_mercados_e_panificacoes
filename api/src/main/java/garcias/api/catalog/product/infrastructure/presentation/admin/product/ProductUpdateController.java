package garcias.api.catalog.product.infrastructure.presentation.admin.product;

import garcias.api.catalog.product.application.dto.requests.UpdateProductRequest;
import garcias.api.catalog.product.application.dto.responses.ProductAdminResponse;
import garcias.api.catalog.product.application.mapper.ProductAdminResponseMapper;
import garcias.api.catalog.product.application.usecases.product.UpdateProductUseCase;
import garcias.api.catalog.product.application.validation.WebpImageValidator;
import garcias.api.catalog.product.domain.entities.Product;
import garcias.api.catalog.product.domain.valueobjects.ProductId;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import jakarta.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/admin/products")
public class ProductUpdateController {


    private final UpdateProductUseCase updateProductUseCase;
    private final WebpImageValidator webpImageValidator;


    public ProductUpdateController(
            UpdateProductUseCase updateProductUseCase,
            WebpImageValidator webpImageValidator)
    {
        this.updateProductUseCase = updateProductUseCase;
        this.webpImageValidator = webpImageValidator;
    }


    @PatchMapping(
            value = "/{id}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @Operation(
            summary = "Update product",
            description = """
                    Updates one or more product fields.

                    All fields are optional.

                    If a new image is sent,
                    the previous image will be replaced.
                    """
    )
    @ApiResponse(
            responseCode = "200",
            description = "Product updated successfully",
            content = @Content(
                    schema = @Schema(
                            implementation = ProductAdminResponse.class
                    )
            )
    )
    @ApiResponse(
            responseCode = "404",
            description = "Product not found"
    )
    @ApiResponse(
            responseCode = "400",
            description = "Invalid product data"
    )
    public ResponseEntity<ProductAdminResponse> update(

            @PathVariable Long id,

            @Valid
            @ModelAttribute
            UpdateProductRequest request

    ) {


        if (request.photo() != null && !request.photo().isEmpty()) {
            webpImageValidator.validate(request.photo());
        }


        Product product =
                updateProductUseCase.execute(
                        new ProductId(id),
                        request
                );


        return ResponseEntity.ok(
                ProductAdminResponseMapper.toResponse(product)
        );
    }
}