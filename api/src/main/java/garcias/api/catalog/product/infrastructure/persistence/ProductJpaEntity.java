package garcias.api.catalog.product.infrastructure.persistence;

import garcias.api.catalog.category.infrastructure.persistence.CategoryJpaEntity;
import garcias.api.catalog.product.domain.enums.ProductStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(
        name = "products",
        indexes = {
                @Index(
                        name = "idx_products_catalog",
                        columnList = "category_id,status,position"
                )
        }
)
public class ProductJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            nullable = false,
            length = 150
    )
    private String name;

    @Column(
            precision = 10,
            scale = 3
    )
    private BigDecimal weight;

    @Column(
            nullable = false
    )
    private Long position;

    @Column(
            nullable = false,
            length = 500
    )
    private String photo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "category_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_products_category")
    )
    private CategoryJpaEntity category;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 30
    )
    private ProductStatus status;

    protected ProductJpaEntity() {
    }

    public static ProductJpaEntity create(

            String name,

            BigDecimal weight,

            Long position,

            String photo,

            CategoryJpaEntity category,

            ProductStatus status

    ) {

        ProductJpaEntity entity =
                new ProductJpaEntity();

        entity.name = name;
        entity.weight = weight;
        entity.position = position;
        entity.photo = photo;
        entity.category = category;
        entity.status = status;

        return entity;
    }

    public static ProductJpaEntity withId(

            Long id,

            String name,

            BigDecimal weight,

            Long position,

            String photo,

            CategoryJpaEntity category,

            ProductStatus status

    ) {

        if (id == null) {

            throw new IllegalArgumentException(
                    "ID cannot be null for update"
            );

        }

        ProductJpaEntity entity =
                new ProductJpaEntity();

        entity.id = id;
        entity.name = name;
        entity.weight = weight;
        entity.position = position;
        entity.photo = photo;
        entity.category = category;
        entity.status = status;

        return entity;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public Long getPosition() {
        return position;
    }

    public String getPhoto() {
        return photo;
    }

    public CategoryJpaEntity getCategory() {
        return category;
    }

    public ProductStatus getStatus() {
        return status;
    }

    public void setPosition(Long position) {
        this.position = position;
    }
}