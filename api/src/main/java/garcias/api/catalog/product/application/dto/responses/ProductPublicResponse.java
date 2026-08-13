package garcias.api.catalog.product.application.dto.responses;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Produto disponível para exibição pública")
public record ProductPublicResponse(

        @Schema(
                description = "Nome do produto",
                example = "X-Bacon"
        )
        String name,

        @Schema(
                description = "Peso do produto em quilogramas",
                example = "0.350"
        )
        BigDecimal weight,

        @Schema(
                description = "URL da imagem do produto",
                example = "/api/public/products/images/x-bacon.webp"
        )
        String photoUrl,

        @Schema(
                description = "Identificador da categoria",
                example = "1"
        )
        String categoryName,

        @Schema(
                description = "Posição do produto no catálogo",
                example = "5"
        )
        Long position

) {}