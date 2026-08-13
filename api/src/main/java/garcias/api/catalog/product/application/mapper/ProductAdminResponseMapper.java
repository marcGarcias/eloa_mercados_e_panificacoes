package garcias.api.catalog.product.application.mapper;

import garcias.api.catalog.product.application.dto.responses.ProductAdminResponse;
import garcias.api.catalog.product.domain.entities.Product;


public final class ProductAdminResponseMapper {


    private ProductAdminResponseMapper() {}


    public static ProductAdminResponse toResponse(Product product) {

        return new ProductAdminResponse(

                product.getId().value(),

                product.getName().value(),

                product.getWeight().value(),

                product.getPosition().value(),

                product.getPhoto().value(),

                product.getCategoryName().value(),

                product.getStatus()
        );
    }
}