package garcias.api.catalog.category.application.services.category;

import garcias.api.catalog.category.application.usecases.category.SearchCategoriesUseCase;
import garcias.api.catalog.category.domain.entities.Category;
import garcias.api.catalog.category.domain.persistence.CategoryRepository;
import garcias.api.shared.config.CacheNames;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchCategoriesService
        implements SearchCategoriesUseCase {

    private final CategoryRepository categoryRepository;

    public SearchCategoriesService(
            CategoryRepository categoryRepository
    ) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Cacheable(value = CacheNames.HOME_CATEGORIES, key = "'all'")
    public List<Category> execute() {
        return categoryRepository.findAll();
    }

    @Override
    public Page<Category> execute(String name, Pageable pageable) {
        return categoryRepository.findAll(name, pageable);
    }
}
