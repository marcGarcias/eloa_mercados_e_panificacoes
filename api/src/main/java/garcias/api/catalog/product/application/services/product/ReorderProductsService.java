package garcias.api.catalog.product.application.services.product;

import garcias.api.catalog.product.application.dto.requests.ReorderProductsRequest;
import garcias.api.catalog.product.application.usecases.product.ReorderProductsUseCase;
import garcias.api.catalog.product.domain.entities.Product;
import garcias.api.catalog.product.domain.repositories.ProductRepository;
import garcias.api.catalog.product.domain.valueobjects.CatalogPosition;
import garcias.api.catalog.product.domain.valueobjects.ProductId;
import garcias.api.shared.exceptions.ObjectNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReorderProductsService implements ReorderProductsUseCase {


    private final ProductRepository productRepository;


    public ReorderProductsService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }


    @Override
    @Transactional
    public void execute(ReorderProductsRequest request) {

        List<Long> rawIds = request.order();

        List<ProductId> productIds =
                rawIds.stream()
                        .map(ProductId::new)
                        .toList();


        List<Product> found =
                productRepository.findAllByIds(productIds);


        if (found.size() != rawIds.size()) {

            List<Long> foundIds =
                    found.stream()
                            .map(p -> p.getId().value())
                            .toList();

            Long missing =
                    rawIds.stream()
                            .filter(id -> !foundIds.contains(id))
                            .findFirst()
                            .orElse(null);

            throw new ObjectNotFoundException(missing);
        }


        Map<ProductId, CatalogPosition> newPositions = new HashMap<>();

        for (int i = 0; i < rawIds.size(); i++) {
            newPositions.put(
                    new ProductId(rawIds.get(i)),
                    new CatalogPosition((long) (i + 1))
            );
        }


        productRepository.reorderAll(newPositions);
    }
}
