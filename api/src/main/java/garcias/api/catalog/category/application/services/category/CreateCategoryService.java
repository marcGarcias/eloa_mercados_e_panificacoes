package garcias.api.catalog.category.application.services.category;

import garcias.api.catalog.category.application.dto.requests.CreateCategoryRequest;
import garcias.api.catalog.category.application.usecases.category.CreateCategoryUseCase;
import garcias.api.catalog.category.domain.entities.Category;
import garcias.api.catalog.category.domain.persistence.CategoryRepository;
import garcias.api.catalog.category.domain.valueobjects.CategoryId;
import garcias.api.catalog.category.domain.valueobjects.CategoryName;
import garcias.api.shared.config.CacheNames;
import garcias.api.shared.exceptions.ObjectAlreadyExistsException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateCategoryService
        implements CreateCategoryUseCase {

    private final CategoryRepository categoryRepository;

    public CreateCategoryService(
            CategoryRepository categoryRepository
    ) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional
    @CacheEvict(value = {CacheNames.HOME_CATEGORIES, CacheNames.HOME_PRODUCTS}, allEntries = true)
    public Category execute(
            CreateCategoryRequest request
    ) {

        CategoryName name =
                new CategoryName(request.name());

        if (categoryRepository.existsByName(name)) {

            throw new ObjectAlreadyExistsException("Category name", name.toString());
        }

        Category category =
                Category.create(
                        CategoryId.empty(),
                        name
                );

        return categoryRepository.save(category);

    }
}
