package garcias.api.catalog.product.application.mapper;


import garcias.api.catalog.product.application.dto.responses.ProductPublicResponse;
import garcias.api.catalog.product.domain.entities.Product;


public final class ProductPublicResponseMapper {


    private ProductPublicResponseMapper(){}


    public static ProductPublicResponse toResponse(Product product){

        return new ProductPublicResponse(

                product.getName().value(),

                product.getWeight().value(),

                product.getPhoto().value(),

                product.getCategoryName().value(),

                product.getPosition().value()
        );
    }
}