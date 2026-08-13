package garcias.api.catalog.product.application.dto.requests;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

public record CreateProductRequest(

        @NotBlank(message = "Name is required")
        String name,

        @NotNull(message = "Weight is required")
        @DecimalMin(value = "0.001", message = "Weight must be greater than zero")
        BigDecimal weight,

        @NotNull(message = "Image is required")
        MultipartFile photo,

        @NotNull(message = "Category is required")
        Long categoryId

) {
}