package garcias.api.catalog.product.infrastructure.presentation.web.product;

import garcias.api.catalog.product.application.dto.requests.PageRequestFilter;
import garcias.api.catalog.product.application.dto.responses.ProductPublicResponse;
import garcias.api.catalog.product.application.mapper.ProductPublicResponseMapper;
import garcias.api.catalog.product.application.usecases.product.SearchProductsUseCase;
import garcias.api.catalog.category.domain.valueobjects.CategoryName;
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
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public/products")
public class ProductPublicSearchController {

    private final SearchProductsUseCase searchProductsUseCase;

    public ProductPublicSearchController(
            SearchProductsUseCase searchProductsUseCase
    ) {
        this.searchProductsUseCase = searchProductsUseCase;
    }

    @Operation(
            summary = "Pesquisar produtos",
            description = """
                    Retorna uma lista paginada de produtos públicos.

                    Todos os filtros são opcionais e podem ser utilizados em conjunto.
                    Caso nenhum filtro seja informado, todos os produtos serão retornados
                    respeitando a paginação.
                    """
    )
    @ApiResponse(
            responseCode = "200",
            description = "Produtos encontrados com sucesso",
            content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(
                            schema = @Schema(implementation = ProductPublicResponse.class)
                    )
            )
    )
    @GetMapping
    public ResponseEntity<Page<ProductPublicResponse>> search(

            @Parameter(
                    in = ParameterIn.QUERY,
                    description = "Nome do produto (busca parcial)",
                    example = "X-Bacon"
            )
            @RequestParam(required = false)
            String name,

            @Parameter(
                    in = ParameterIn.QUERY,
                    description = "Identificador da categoria",
                    example = "2"
            )
            @RequestParam(required = false)
            String categoryName,

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

    ) {

        ProductFilter filter = new ProductFilter(
                name,
                null,
                categoryName != null
                        ? new CategoryName(categoryName)
                        : null,
                status
        );

        Page<Product> products =
                searchProductsUseCase.execute(
                        filter,
                        new PageRequestFilter(page, size)
                );

        return ResponseEntity.ok(
                products.map(ProductPublicResponseMapper::toResponse)
        );
    }
}