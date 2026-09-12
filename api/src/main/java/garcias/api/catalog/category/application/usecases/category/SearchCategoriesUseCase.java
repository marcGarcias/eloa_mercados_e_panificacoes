package garcias.api.catalog.category.application.usecases.category;

import garcias.api.catalog.category.domain.entities.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SearchCategoriesUseCase {

    List<Category> execute();

    Page<Category> execute(String name, Pageable pageable);
}
