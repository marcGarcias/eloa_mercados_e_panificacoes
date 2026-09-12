package garcias.api.catalog.product.application.services.product;

import garcias.api.catalog.product.application.dto.requests.PageRequestFilter;
import garcias.api.catalog.product.application.usecases.product.SearchProductsUseCase;
import garcias.api.catalog.product.domain.entities.Product;
import garcias.api.catalog.product.domain.repositories.ProductRepository;
import garcias.api.catalog.product.domain.valueobjects.ProductFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class SearchProductsService implements SearchProductsUseCase {

    private final ProductRepository productRepository;

    public SearchProductsService(
            ProductRepository productRepository
    ) {
        this.productRepository = productRepository;
    }

    @Override
    public Page<Product> execute(
            ProductFilter filter,
            PageRequestFilter pageRequest
    ) {

        return productRepository.search(
                filter,
                PageRequest.of(
                        pageRequest.page(),
                        pageRequest.size(),
                        Sort.by(Sort.Direction.ASC, "position")
                )
        );
    }
}
