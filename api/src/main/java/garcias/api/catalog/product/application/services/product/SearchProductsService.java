package garcias.api.catalog.product.application.services.product;

import garcias.api.catalog.product.application.dto.requests.PageRequestFilter;
import garcias.api.catalog.product.application.usecases.product.SearchProductsUseCase;
import garcias.api.catalog.category.domain.persistence.CategoryRepository;
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
    private  final CategoryRepository categoryRepository;


    public SearchProductsService(
            ProductRepository productRepository, CategoryRepository categoryRepository
    ) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
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