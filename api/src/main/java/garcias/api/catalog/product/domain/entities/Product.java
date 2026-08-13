package garcias.api.catalog.product.domain.entities;

import java.util.Objects;

import garcias.api.catalog.category.domain.valueobjects.CategoryId;
import garcias.api.catalog.category.domain.valueobjects.CategoryName;
import garcias.api.catalog.product.domain.enums.ProductStatus;
import garcias.api.catalog.product.domain.valueobjects.CatalogPosition;
import garcias.api.catalog.product.domain.valueobjects.ProductId;
import garcias.api.catalog.product.domain.valueobjects.ProductName;
import garcias.api.catalog.product.domain.valueobjects.ProductPhoto;
import garcias.api.catalog.product.domain.valueobjects.ProductWeight;

public class Product {

    private final ProductId id;
    private ProductName name;
    private ProductWeight weight;
    private CatalogPosition position;
    private ProductStatus status;
    private CategoryId categoryId;
    private CategoryName categoryName;
    private ProductPhoto photo;

    public ProductId getId() {
        return id;
    }

    public ProductName getName() {
        return name;
    }

    public ProductWeight getWeight() {
        return weight;
    }

    public CatalogPosition getPosition() {return position;}

    public ProductStatus getStatus() {
        return status;
    }

    public CategoryId getCategoryId() {
        return categoryId;
    }

    public CategoryName getCategoryName() {
        return categoryName;
    }

    public ProductPhoto getPhoto() {
        return photo;
    }

    public Product(
            ProductId productId,
            ProductName name,
            ProductWeight weight,
            CatalogPosition position,
            CategoryId categoryId,
            CategoryName categoryName,
            ProductStatus status,
            ProductPhoto photo

    ) {
        this.id = Objects.requireNonNull(productId);
        this.name = Objects.requireNonNull(name);
        this.weight = Objects.requireNonNull(weight);
        this.position = Objects.requireNonNull(position);
        this.photo = Objects.requireNonNull(photo);
        this.categoryId = Objects.requireNonNull(categoryId);
        this.categoryName = categoryName;
        this.status = Objects.requireNonNull(status);
    }

    public static Product create(

            ProductName name,

            ProductWeight weight,

            CatalogPosition position,

            ProductPhoto photo,

            CategoryId categoryId

    ) {

        return new Product(
                ProductId.empty(),
                name,
                weight,
                position,
                categoryId,
                null,
                ProductStatus.ACTIVE,
                photo
        );

    }

    public void changeStatus(ProductStatus status) {

        if(status == ProductStatus.ACTIVE) {
            activate();
            return;
        }

        deactivate();
    }

    public void rename(ProductName newName) {
        this.name = Objects.requireNonNull(newName);
    }

    public void changeWeight(ProductWeight newWeight) {
        this.weight = Objects.requireNonNull(newWeight);
    }

    public void changePosition(CatalogPosition position) { this.position = Objects.requireNonNull(position);}

    public void changePhoto(ProductPhoto newPhoto) {
        this.photo = Objects.requireNonNull(newPhoto);
    }

    public void changeCategory(CategoryId categoryId) {
        this.categoryId = Objects.requireNonNull(categoryId);
    }

    public void activate() {
        status = ProductStatus.ACTIVE;
    }

    public void deactivate() {
        status = ProductStatus.INACTIVE;
    }

    public boolean isActive() {
        return status == ProductStatus.ACTIVE;
    }

}
