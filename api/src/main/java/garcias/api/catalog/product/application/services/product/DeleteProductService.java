package garcias.api.catalog.product.application.services.product;


import garcias.api.catalog.product.application.storage.ImageStorage;
import garcias.api.catalog.product.application.usecases.product.DeleteProductUseCase;
import garcias.api.catalog.product.domain.entities.Product;
import garcias.api.shared.exceptions.ObjectNotFoundException;
import garcias.api.catalog.product.domain.repositories.ProductRepository;
import garcias.api.catalog.product.domain.valueobjects.CatalogPosition;
import garcias.api.catalog.product.domain.valueobjects.ProductId;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;


@Service
public class DeleteProductService implements DeleteProductUseCase {


    private final ProductRepository productRepository;
    private final ImageStorage imageStorage;

    public DeleteProductService(
            ProductRepository productRepository, ImageStorage imageStorage
    ) {
        this.productRepository = productRepository;
        this.imageStorage = imageStorage;
    }


    @Override
    @Transactional
    public void execute(ProductId id) {


        Product product =
                productRepository.findById(id)
                        .orElseThrow(() -> new ObjectNotFoundException(id.value()));

        CatalogPosition position = product.getPosition();

        productRepository.delete(product);

        productRepository.reorganizePositionsAfterDelete(position);

        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        imageStorage.delete(product.getPhoto().value());
                    }
                }
        );
    }
}