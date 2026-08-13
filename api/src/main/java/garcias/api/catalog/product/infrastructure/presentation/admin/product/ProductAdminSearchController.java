package garcias.api.catalog.product.infrastructure.presentation.admin.product;

import garcias.api.catalog.product.application.dto.requests.PageRequestFilter;
import garcias.api.catalog.product.application.dto.responses.ProductAdminResponse;
import garcias.api.catalog.product.application.mapper.ProductAdminResponseMapper;
import garcias.api.catalog.product.application.usecases.product.SearchProductsUseCase;
import garcias.api.catalog.category.domain.valueobjects.CategoryId;
import garcias.api.catalog.product.domain.entities.Product;
import garcias.api.catalog.product.domain.enums.ProductStatus;
import garcias.api.catalog.product.domain.valueobjects.ProductFilter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/products")
public class ProductAdminSearchController {


    private final SearchProductsUseCase searchProductsUseCase;


    public ProductAdminSearchController(
            SearchProductsUseCase searchProductsUseCase
    ){
        this.searchProductsUseCase = searchProductsUseCase;
    }


    @Operation(
            summary = "Pesquisar produtos (Admin)",
            description = """
                    Retorna uma lista paginada de produtos para administração.

                    Todos os filtros são opcionais e podem ser utilizados em conjunto.
                    """
    )
    @ApiResponse(
            responseCode = "200",
            description = "Produtos encontrados com sucesso",
            content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(
                            schema = @Schema(implementation = ProductAdminResponse.class)
                    )
            )
    )
    @GetMapping
    public ResponseEntity<Page<ProductAdminResponse>> search(

            @Parameter(
                    in = ParameterIn.QUERY,
                    description = "Nome do produto (busca parcial)",
                    example = "X-Bacon"
            )
            @RequestParam(required = false)
            String name,

            @Parameter(
                    in = ParameterIn.QUERY,
                    description = "ID da categoria",
                    example = "1"
            )
            @RequestParam(required = false)
            Long categoryId,

            @Parameter(
                    in = ParameterIn.QUERY,
                    description = "Status do produto",
                    example = "ACTIVE"
            )
            @RequestParam(required = false)
            ProductStatus status,

            @Parameter(
                    in = ParameterIn.QUERY,
                    description = "Número da página (inicia em 0)",
                    example = "0"
            )
            @RequestParam(defaultValue = "0")
            int page,

            @Parameter(
                    in = ParameterIn.QUERY,
                    description = "Quantidade de registros por página",
                    example = "10"
            )
            @RequestParam(defaultValue = "10")
            int size

    ){


        ProductFilter filter =
                new ProductFilter(
                        name,
                        categoryId != null
                                ? new CategoryId(categoryId)
                                : null,
                        null,
                        status
                );


        Page<Product> products =
                searchProductsUseCase.execute(
                        filter,
                        new PageRequestFilter(page, size)
                );


        return ResponseEntity.ok(
                products.map(
                        ProductAdminResponseMapper::toResponse
                )
        );
    }
}