package garcias.api.catalog.product.application.usecases.product;

import garcias.api.catalog.product.application.dto.requests.BatchDeleteProductsRequest;

public interface BatchDeleteProductsUseCase {

    void execute(BatchDeleteProductsRequest request);

}
