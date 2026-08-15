package garcias.api.catalog.category.infrastructure.persistence;

import garcias.api.catalog.category.infrastructure.exceptions.InvalidCategoryEntityStateException;
import jakarta.persistence.*;

@Entity
@Table(
        name = "categories",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_category_name",
                        columnNames = "name"
                )
        }
)
public class CategoryJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            nullable = false,
            length = 50
    )
    private String name;

    protected CategoryJpaEntity() {
    }

    public static CategoryJpaEntity create(
            String name
    ) {

        CategoryJpaEntity entity =
                new CategoryJpaEntity();

        entity.name = name;

        return entity;
    }

    public static CategoryJpaEntity reference(
            Long id
    ) {

        if (id == null) {
            throw new InvalidCategoryEntityStateException(
                    "ID cannot be null"
            );
        }

        CategoryJpaEntity entity =
                new CategoryJpaEntity();

        entity.id = id;

        return entity;
    }

    public static CategoryJpaEntity withId(
            Long id,
            String name
    ) {

        if (id == null) {
            throw new InvalidCategoryEntityStateException(
                    "ID cannot be null for update"
            );
        }

        CategoryJpaEntity entity =
                new CategoryJpaEntity();

        entity.id = id;
        entity.name = name;

        return entity;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}