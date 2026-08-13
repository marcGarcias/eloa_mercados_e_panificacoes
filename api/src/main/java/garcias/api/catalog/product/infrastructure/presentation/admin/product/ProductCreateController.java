package garcias.api.catalog.product.infrastructure.presentation.admin.product;

import garcias.api.catalog.product.application.dto.requests.CreateProductRequest;
import garcias.api.catalog.product.application.dto.responses.ProductAdminResponse;
import garcias.api.catalog.product.application.mapper.ProductAdminResponseMapper;
import garcias.api.catalog.product.application.usecases.product.CreateProductUseCase;
import garcias.api.catalog.product.domain.entities.Product;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/admin/products")
public class ProductCreateController {


    private final CreateProductUseCase createProductUseCase;


    public ProductCreateController(
            CreateProductUseCase createProductUseCase
    ) {
        this.createProductUseCase = createProductUseCase;
    }


    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Create product",
            description = """
                    Creates a new product.

                    The image is uploaded as multipart/form-data,
                    stored on the server and only its path is saved
                    in the database.
                    """
    )
    @ApiResponse(
            responseCode = "201",
            description = "Product created successfully",
            content = @Content(
                    schema = @Schema(
                            implementation = ProductAdminResponse.class
                    )
            )
    )
    @ApiResponse(
            responseCode = "400",
            description = "Invalid request"
    )
    public ResponseEntity<ProductAdminResponse> create(

            @Valid
            @ModelAttribute
            CreateProductRequest request

    ) {


        Product product =
                createProductUseCase.execute(request);


        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ProductAdminResponseMapper.toResponse(product)
                );
    }
}