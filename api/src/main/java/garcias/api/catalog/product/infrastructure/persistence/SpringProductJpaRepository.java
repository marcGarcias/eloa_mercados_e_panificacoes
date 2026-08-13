package garcias.api.catalog.product.infrastructure.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SpringProductJpaRepository
        extends JpaRepository<ProductJpaEntity, Long>,
        JpaSpecificationExecutor<ProductJpaEntity> {

    @EntityGraph(attributePaths = {"category"})
    Page<ProductJpaEntity> findAll(Specification<ProductJpaEntity> spec, Pageable pageable);

    @EntityGraph(attributePaths = {"category"})
    @Query("SELECT p FROM ProductJpaEntity p WHERE p.id = :id")
    Optional<ProductJpaEntity> findByIdWithCategory(@Param("id") Long id);

    Optional<ProductJpaEntity> findFirstByOrderByPositionDesc();

    List<ProductJpaEntity> findByPositionGreaterThan(Long position);

    boolean existsByCategory_Id(Long categoryId);
}