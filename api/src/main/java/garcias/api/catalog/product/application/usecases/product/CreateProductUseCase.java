package garcias.api.catalog.product.application.usecases.product;

import garcias.api.catalog.product.domain.entities.Product;
import garcias.api.catalog.product.application.dto.requests.CreateProductRequest;

public interface CreateProductUseCase {

    Product execute(CreateProductRequest request);
}