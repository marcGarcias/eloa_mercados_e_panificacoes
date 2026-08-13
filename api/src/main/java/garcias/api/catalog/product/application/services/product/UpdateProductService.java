package garcias.api.catalog.product.application.services.product;

import garcias.api.catalog.product.application.dto.requests.UpdateProductRequest;
import garcias.api.catalog.product.application.storage.ImageStorage;
import garcias.api.catalog.product.application.usecases.product.UpdateProductUseCase;
import garcias.api.catalog.category.domain.valueobjects.CategoryId;
import garcias.api.catalog.product.domain.entities.Product;
import garcias.api.catalog.product.domain.repositories.ProductRepository;
import garcias.api.catalog.product.domain.valueobjects.*;
import garcias.api.shared.exceptions.ObjectNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class UpdateProductService implements UpdateProductUseCase {


    private final ProductRepository productRepository;
    private final ImageStorage imageStorage;

    public UpdateProductService(
            ProductRepository productRepository, ImageStorage imageStorage
    ) {
        this.productRepository = productRepository;
        this.imageStorage = imageStorage;
    }


    @Override
    @Transactional
    public Product execute(
            ProductId id,
            UpdateProductRequest request
    ) {


        Product product =
                productRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new ObjectNotFoundException(id.value())
                        );


        if (request.name() != null && !request.name().isBlank()) {

            product.rename(
                    new ProductName(
                            request.name().trim()
                    )
            );
        }


        if (request.weight() != null) {

            product.changeWeight(
                    new ProductWeight(request.weight())
            );
        }


        String oldPhoto = null;


        if (request.photo() != null && !request.photo().isEmpty()) {

            oldPhoto = product.getPhoto().value();

            String newPhoto =
                    imageStorage.save(request.photo());


            product.changePhoto(
                    new ProductPhoto(newPhoto)
            );
        }


        if (request.categoryId() != null) {

            product.changeCategory(
                    new CategoryId(request.categoryId())
            );
        }


        if (request.position() != null) {

            CatalogPosition newPosition =
                    new CatalogPosition(request.position());


            if (!newPosition.equals(product.getPosition())) {

                productRepository.updatePosition(
                        product,
                        newPosition
                );
            }
        }


        if (request.status() != null) {

            product.changeStatus(
                    request.status()
            );
        }


        Product updated =
                productRepository.save(product);


        if (oldPhoto != null) {

            imageStorage.delete(oldPhoto);
        }

        return updated;
    }
}