package garcias.api.catalog.category.application.services.category;

import garcias.api.catalog.category.application.dto.requests.BatchDeleteCategoriesRequest;
import garcias.api.catalog.category.application.usecases.category.BatchDeleteCategoriesUseCase;
import garcias.api.catalog.category.domain.entities.Category;
import garcias.api.catalog.category.domain.exceptions.CategoryHasProductsException;
import garcias.api.catalog.category.domain.persistence.CategoryRepository;
import garcias.api.catalog.product.domain.repositories.ProductRepository;
import garcias.api.catalog.category.domain.valueobjects.CategoryId;
import garcias.api.shared.exceptions.ObjectNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BatchDeleteCategoriesService implements BatchDeleteCategoriesUseCase {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public BatchDeleteCategoriesService(
            CategoryRepository categoryRepository,
            ProductRepository productRepository
    ) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public void execute(BatchDeleteCategoriesRequest request) {
        List<CategoryId> categoryIds = request.ids().stream()
                .map(CategoryId::new)
                .toList();

        for (CategoryId categoryId : categoryIds) {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new ObjectNotFoundException(categoryId.value()));

            if (productRepository.existsByCategoryId(categoryId)) {
                throw new CategoryHasProductsException(categoryId);
            }

            categoryRepository.delete(category);
        }
    }
}
