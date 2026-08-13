package garcias.api.catalog.product.application.dto.requests;

import garcias.api.catalog.product.domain.enums.ProductStatus;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

public record UpdateProductRequest(

        String name,

        BigDecimal weight,

        MultipartFile photo,

        Long categoryId,

        ProductStatus status,

        Long position

) {
}