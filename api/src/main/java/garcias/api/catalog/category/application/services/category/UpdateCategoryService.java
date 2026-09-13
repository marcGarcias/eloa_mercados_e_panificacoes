package garcias.api.catalog.category.application.services.category;

import garcias.api.catalog.category.application.dto.requests.UpdateCategoryRequest;
import garcias.api.catalog.category.application.usecases.category.UpdateCategoryUseCase;
import garcias.api.catalog.category.domain.entities.Category;
import garcias.api.catalog.category.domain.persistence.CategoryRepository;
import garcias.api.catalog.category.domain.valueobjects.CategoryId;
import garcias.api.catalog.category.domain.valueobjects.CategoryName;
import garcias.api.shared.config.CacheNames;
import garcias.api.shared.exceptions.ObjectAlreadyExistsException;
import garcias.api.shared.exceptions.ObjectNotFoundException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateCategoryService implements UpdateCategoryUseCase {

    private final CategoryRepository categoryRepository;

    public UpdateCategoryService(
            CategoryRepository categoryRepository
    ) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional
    @CacheEvict(value = {CacheNames.HOME_CATEGORIES, CacheNames.HOME_PRODUCTS}, allEntries = true)
    public Category execute(
            CategoryId id,
            UpdateCategoryRequest request
    ) {

        Category category =
                categoryRepository
                        .findById(id)
                        .orElseThrow(
                                () -> new ObjectNotFoundException(id.value())
                        );

        if (request.name() != null) {

            CategoryName newName =
                    new CategoryName(request.name());

            categoryRepository
                    .findByName(newName)
                    .ifPresent(existing -> {

                        if (!existing.getId()
                                .equals(category.getId())) {

                            throw new ObjectAlreadyExistsException("Category name", newName.toString());
                        }
                    });

            category.rename(newName);
        }

        return categoryRepository.save(category);
    }
}
