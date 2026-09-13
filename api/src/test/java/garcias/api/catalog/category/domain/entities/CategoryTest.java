package garcias.api.catalog.category.domain.entities;

import garcias.api.catalog.category.domain.valueobjects.CategoryId;
import garcias.api.catalog.category.domain.valueobjects.CategoryName;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Category Domain Entity Unit Tests")
class CategoryTest {

    @Test
    @DisplayName("Deve instanciar Category através do construtor com sucesso")
    void shouldConstructCategorySuccessfully() {
        CategoryId id = new CategoryId(1L);
        CategoryName name = new CategoryName("Bebidas");

        Category category = new Category(id, name);

        assertThat(category.getId()).isEqualTo(id);
        assertThat(category.getName()).isEqualTo(name);
    }

    @Test
    @DisplayName("Deve instanciar Category através do método factory create com sucesso")
    void shouldCreateCategoryViaFactorySuccessfully() {
        CategoryId id = CategoryId.empty();
        CategoryName name = new CategoryName("Frios e Laticínios");

        Category category = Category.create(id, name);

        assertThat(category.getId()).isEqualTo(id);
        assertThat(category.getName()).isEqualTo(name);
    }

    @Test
    @DisplayName("Deve lançar exceção quando id for nulo no construtor")
    void shouldThrowWhenIdIsNull() {
        CategoryName name = new CategoryName("Hortifruti");

        assertThatThrownBy(() -> new Category(null, name))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("Deve lançar exceção quando nome for nulo no construtor")
    void shouldThrowWhenNameIsNull() {
        CategoryId id = new CategoryId(1L);

        assertThatThrownBy(() -> new Category(id, null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("Deve renomear categoria com sucesso")
    void shouldRenameCategorySuccessfully() {
        Category category = Category.create(new CategoryId(1L), new CategoryName("Pães"));
        CategoryName newName = new CategoryName("Pães Artesanais");

        category.rename(newName);

        assertThat(category.getName()).isEqualTo(newName);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar renomear com nome nulo")
    void shouldThrowWhenRenameWithNull() {
        Category category = Category.create(new CategoryId(1L), new CategoryName("Pães"));

        assertThatThrownBy(() -> category.rename(null))
                .isInstanceOf(NullPointerException.class);
    }
}
