package garcias.api.catalog.category.application.services.category;


import garcias.api.catalog.category.application.usecases.category.SearchCategoriesUseCase;
import garcias.api.catalog.category.domain.entities.Category;
import garcias.api.catalog.category.domain.persistence.CategoryRepository;

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
    public List<Category> execute() {

        return categoryRepository.findAll();
    }
}