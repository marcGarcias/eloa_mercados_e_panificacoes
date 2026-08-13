package garcias.api.catalog.product.application.usecases.product;

import garcias.api.catalog.product.application.dto.requests.UpdateProductRequest;
import garcias.api.catalog.product.domain.entities.Product;

import garcias.api.catalog.product.domain.valueobjects.ProductId;

public interface UpdateProductUseCase {

    Product execute(
            ProductId id,
            UpdateProductRequest request
    );

}