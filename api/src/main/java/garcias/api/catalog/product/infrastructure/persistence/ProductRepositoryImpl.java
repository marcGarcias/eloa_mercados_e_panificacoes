package garcias.api.catalog.product.infrastructure.persistence;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import garcias.api.catalog.category.domain.valueobjects.CategoryId;
import garcias.api.catalog.product.domain.entities.Product;
import garcias.api.catalog.product.domain.repositories.ProductRepository;
import garcias.api.catalog.product.domain.valueobjects.CatalogPosition;
import garcias.api.catalog.product.domain.valueobjects.ProductFilter;
import garcias.api.catalog.product.domain.valueobjects.ProductId;
import garcias.api.catalog.product.infrastructure.mapper.ProductMapper;
import garcias.api.catalog.product.infrastructure.persistence.specification.ProductSpecification;
import jakarta.persistence.EntityManager;

@Repository
public class ProductRepositoryImpl implements ProductRepository {

    private final SpringProductJpaRepository repository;
    private final EntityManager entityManager;

    public ProductRepositoryImpl(
            SpringProductJpaRepository repository,
            EntityManager entityManager
    ) {
        this.repository = repository;
        this.entityManager = entityManager;
    }

    @Override
    public Product save(Product product) {
        var entity = ProductMapper.toEntity(product);
        var saved = repository.save(entity);
        
        entityManager.flush();
        entityManager.clear();
        
        var savedWithCategory = repository.findByIdWithCategory(saved.getId()).orElse(saved);
        return ProductMapper.toDomain(savedWithCategory);
    }

    @Override
    public Optional<Product> findById(ProductId id) {
        if (id == null || id.value() == null) return Optional.empty();

        return repository.findByIdWithCategory(id.value())
                .map(ProductMapper::toDomain);
    }

    @Override
    public void delete(Product product) {
        if (product == null || product.getId() == null) {
            return;
        }
        repository.deleteById(product.getId().value());
    }

    @Override
    public Optional<CatalogPosition> findLastPosition() {

        return repository
                .findFirstByOrderByPositionDesc()
                .map(ProductJpaEntity::getPosition)
                .map(CatalogPosition::new);
    }

    @Override
    public Page<Product> search(
            ProductFilter filter,
            Pageable pageable
    ) {

        return repository
                .findAll(
                        ProductSpecification.from(filter),
                        pageable
                )
                .map(ProductMapper::toDomain);
    }

    @Override
    @Transactional
    public void updatePosition(
            Product product,
            CatalogPosition newPosition
    ) {

        Long oldPosition =
                product.getPosition().value();


        Long position =
                newPosition.value();


        if(oldPosition.equals(position)) {
            return;
        }


        List<ProductJpaEntity> products =
                repository.findAll(
                        Sort.by("position")
                );


        if(position < oldPosition) {


            products.stream()
                    .filter(p ->
                            p.getPosition() >= position &&
                                    p.getPosition() < oldPosition
                    )
                    .forEach(p ->
                            p.setPosition(
                                    p.getPosition() + 1
                            )
                    );


        } else {


            products.stream()
                    .filter(p ->
                            p.getPosition() > oldPosition &&
                                    p.getPosition() <= position
                    )
                    .forEach(p ->
                            p.setPosition(
                                    p.getPosition() - 1
                            )
                    );
        }


        product.changePosition(newPosition);
    }

    @Override
    @Transactional
    public void reorganizePositionsAfterDelete(
            CatalogPosition position
    ) {

        List<ProductJpaEntity> products =
                repository.findByPositionGreaterThan(
                        position.value()
                );


        products.forEach(product ->
                product.setPosition(
                        product.getPosition() - 1
                )
        );
    }

    @Override
    public boolean existsByCategoryId(CategoryId categoryId) {

        if (categoryId == null || categoryId.isEmpty()) {
            return false;
        }

        return repository.existsByCategory_Id(categoryId.value());
    }

    @Override
    public List<Product> findAllByIds(List<ProductId> ids) {

        List<Long> rawIds =
                ids.stream()
                        .map(ProductId::value)
                        .toList();

        return repository
                .findAllByIdIn(rawIds)
                .stream()
                .map(ProductMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional
    public void reorderAll(Map<ProductId, CatalogPosition> newPositions) {

        List<Long> rawIds =
                newPositions.keySet()
                        .stream()
                        .map(ProductId::value)
                        .toList();

        List<ProductJpaEntity> entities =
                repository.findAllByIdIn(rawIds);

        for (ProductJpaEntity entity : entities) {

            CatalogPosition newPosition =
                    newPositions.get(new ProductId(entity.getId()));

            if (newPosition != null) {
                entity.setPosition(newPosition.value());
            }
        }

        // As entidades sao gerenciadas pelo EntityManager dentro da transacao,
        // o flush automatico persiste as mudancas sem necessidade de save() explicito.
        entityManager.flush();
    }

    @Override
    @Transactional
    public void reorganizeAllPositions() {
        List<ProductJpaEntity> remaining = repository.findAll(
                Sort.by("position")
        );
        for (int i = 0; i < remaining.size(); i++) {
            remaining.get(i).setPosition((long) (i + 1));
        }
        entityManager.flush();
    }
}
