package garcias.api.catalog.category.application.usecases.category;


import garcias.api.catalog.category.domain.entities.Category;

import java.util.List;

public interface SearchCategoriesUseCase {

    List<Category> execute();

}