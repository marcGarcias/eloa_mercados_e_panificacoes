package garcias.api.catalog.category.domain.persistence;

import garcias.api.catalog.category.domain.entities.Category;
import garcias.api.catalog.category.domain.valueobjects.CategoryId;
import garcias.api.catalog.category.domain.valueobjects.CategoryName;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository {

    Category save(Category category);

    Optional<Category> findById(CategoryId id);

    Optional<Category> findByName(CategoryName name);

    boolean existsByName(CategoryName name);

    List<Category> findAll();

    void delete(Category category);
}