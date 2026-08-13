package garcias.api.catalog.category.application.services.category;

import garcias.api.catalog.category.application.usecases.category.DeleteCategoryUseCase;
import garcias.api.catalog.category.domain.entities.Category;
import garcias.api.catalog.category.domain.exceptions.CategoryHasProductsException;
import garcias.api.shared.exceptions.ObjectNotFoundException;
import garcias.api.catalog.category.domain.persistence.CategoryRepository;
import garcias.api.catalog.product.domain.repositories.ProductRepository;
import garcias.api.catalog.category.domain.valueobjects.CategoryId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeleteCategoryService implements DeleteCategoryUseCase {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public DeleteCategoryService(
            CategoryRepository categoryRepository,
            ProductRepository productRepository
    ) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public void execute(CategoryId categoryId) {


        Category category =
                categoryRepository
                        .findById(categoryId)
                        .orElseThrow(
                                () -> new ObjectNotFoundException(categoryId.value())
                        );


        if (productRepository.existsByCategoryId(categoryId)) {

            throw new CategoryHasProductsException(categoryId);
        }


        categoryRepository.delete(category);
    }
}