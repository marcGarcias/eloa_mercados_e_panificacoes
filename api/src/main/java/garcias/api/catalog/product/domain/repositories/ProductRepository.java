package garcias.api.catalog.product.domain.repositories;

import garcias.api.catalog.category.domain.valueobjects.CategoryId;
import garcias.api.catalog.product.domain.entities.Product;
import garcias.api.catalog.product.domain.valueobjects.CatalogPosition;
import garcias.api.catalog.product.domain.valueobjects.ProductFilter;
import garcias.api.catalog.product.domain.valueobjects.ProductId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ProductRepository {


    Product save(Product product);


    Optional<Product> findById(ProductId id);

    void delete(Product product);


    Optional<CatalogPosition> findLastPosition();


    Page<Product> search(
            ProductFilter filter,
            Pageable pageable
    );

    void updatePosition(
            Product product,
            CatalogPosition newPosition
    );

    void reorganizePositionsAfterDelete(
            CatalogPosition deletedPosition
    );

    boolean existsByCategoryId(CategoryId categoryId);
}