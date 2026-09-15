package garcias.api.catalog.product.application.dto.requests;

import garcias.api.catalog.product.domain.enums.ProductStatus;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

public record UpdateProductRequest(

        @Size(
                min = 2,
                max = 32,
                message = "Product name must contain between 2 and 32 characters"
        )
        String name,

        BigDecimal weight,

        MultipartFile photo,

        Long categoryId,

        ProductStatus status,

        Long position

) {
}
