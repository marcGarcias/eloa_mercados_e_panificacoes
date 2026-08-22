package garcias.api.catalog.product.application.usecases.product;

import garcias.api.catalog.product.application.dto.requests.ReorderProductsRequest;

public interface ReorderProductsUseCase {

    void execute(ReorderProductsRequest request);

}
