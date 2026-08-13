package garcias.api.catalog.product.application.usecases.product;

import garcias.api.catalog.product.application.dto.requests.PageRequestFilter;
import garcias.api.catalog.product.domain.entities.Product;
import garcias.api.catalog.product.domain.valueobjects.ProductFilter;
import org.springframework.data.domain.Page;

public interface SearchProductsUseCase {
    Page<Product> execute(
            ProductFilter filter,
            PageRequestFilter pageRequest
    );

}