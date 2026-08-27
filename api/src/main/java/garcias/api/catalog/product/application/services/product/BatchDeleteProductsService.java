package garcias.api.catalog.product.application.services.product;

import garcias.api.catalog.product.application.dto.requests.BatchDeleteProductsRequest;
import garcias.api.catalog.product.application.storage.ImageStorage;
import garcias.api.catalog.product.application.usecases.product.BatchDeleteProductsUseCase;
import garcias.api.catalog.product.domain.entities.Product;
import garcias.api.catalog.product.domain.repositories.ProductRepository;
import garcias.api.catalog.product.domain.valueobjects.ProductId;
import garcias.api.shared.exceptions.ObjectNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.ArrayList;
import java.util.List;

@Service
public class BatchDeleteProductsService implements BatchDeleteProductsUseCase {

    private final ProductRepository productRepository;
    private final ImageStorage imageStorage;

    public BatchDeleteProductsService(
            ProductRepository productRepository,
            ImageStorage imageStorage
    ) {
        this.productRepository = productRepository;
        this.imageStorage = imageStorage;
    }

    @Override
    @Transactional
    public void execute(BatchDeleteProductsRequest request) {
        List<ProductId> productIds = request.ids().stream()
                .map(ProductId::new)
                .toList();

        List<Product> products = productRepository.findAllByIds(productIds);

        if (products.size() != request.ids().size()) {
            List<Long> foundIds = products.stream()
                    .map(p -> p.getId().value())
                    .toList();

            Long missingId = request.ids().stream()
                    .filter(id -> !foundIds.contains(id))
                    .findFirst()
                    .orElse(null);

            throw new ObjectNotFoundException(missingId);
        }

        List<String> photoPaths = new ArrayList<>();

        for (Product product : products) {
            photoPaths.add(product.getPhoto().value());
            productRepository.delete(product);
        }

        // Reorganiza as posicoes de todos os produtos ativos restantes de 1 a N
        productRepository.reorganizeAllPositions();

        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        for (String photoPath : photoPaths) {
                            imageStorage.delete(photoPath);
                        }
                    }
                }
        );
    }
}
