package garcias.api.catalog.product.application.usecases.product;

import garcias.api.catalog.product.domain.valueobjects.ProductId;

public interface DeleteProductUseCase {

    void execute(ProductId id);

}