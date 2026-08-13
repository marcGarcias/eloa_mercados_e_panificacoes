package garcias.api.catalog.category.infrastructure.persistence;

import garcias.api.catalog.category.domain.entities.Category;
import garcias.api.catalog.category.domain.persistence.CategoryRepository;
import garcias.api.catalog.category.domain.valueobjects.CategoryId;
import garcias.api.catalog.category.domain.valueobjects.CategoryName;
import garcias.api.catalog.category.infrastructure.mapper.CategoryMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CategoryRepositoryImpl implements CategoryRepository {

    private final SpringCategoryJpaRepository repository;

    public CategoryRepositoryImpl(
            SpringCategoryJpaRepository repository
    ) {
        this.repository = repository;
    }

    @Override
    public Category save(Category category) {

        var entity = CategoryMapper.toEntity(category);

        var saved = repository.save(entity);

        return CategoryMapper.toDomain(saved);
    }

    @Override
    public Optional<Category> findById(CategoryId id) {

        if (id == null || id.isEmpty()) {
            return Optional.empty();
        }

        return repository.findById(id.value())
                .map(CategoryMapper::toDomain);
    }

    @Override
    public Optional<Category> findByName(CategoryName name) {

        if (name == null) {
            return Optional.empty();
        }

        return repository.findByNameIgnoreCase(name.value())
                .map(CategoryMapper::toDomain);
    }

    @Override
    public boolean existsByName(CategoryName name) {

        if (name == null) {
            return false;
        }

        return repository.existsByNameIgnoreCase(name.value());
    }

    @Override
    public List<Category> findAll() {

        return repository.findAll()
                .stream()
                .map(CategoryMapper::toDomain)
                .toList();
    }

    @Override
    public void delete(Category category) {

        if (category == null || category.getId().isEmpty()) {
            return;
        }

        repository.deleteById(category.getId().value());
    }
}