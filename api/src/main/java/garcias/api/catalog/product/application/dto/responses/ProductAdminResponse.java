package garcias.api.catalog.product.application.dto.responses;

import garcias.api.catalog.product.domain.enums.ProductStatus;

import java.math.BigDecimal;


public record ProductAdminResponse(

        Long id,

        String name,

        BigDecimal weight,

        Long position,

        String photo,

        String categoryName,

        ProductStatus status

) {}