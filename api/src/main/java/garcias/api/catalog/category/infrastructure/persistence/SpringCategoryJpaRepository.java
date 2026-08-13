package garcias.api.catalog.category.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpringCategoryJpaRepository
        extends JpaRepository<CategoryJpaEntity, Long> {

    Optional<CategoryJpaEntity> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);
}