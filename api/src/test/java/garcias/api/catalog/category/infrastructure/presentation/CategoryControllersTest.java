package garcias.api.catalog.category.infrastructure.presentation;

import garcias.api.catalog.category.application.dto.requests.BatchDeleteCategoriesRequest;
import garcias.api.catalog.category.application.dto.requests.CreateCategoryRequest;
import garcias.api.catalog.category.application.dto.requests.UpdateCategoryRequest;
import garcias.api.catalog.category.application.dto.responses.CategoryAdmResponse;
import garcias.api.catalog.category.application.dto.responses.CategoryWebResponse;
import garcias.api.catalog.category.application.usecases.category.BatchDeleteCategoriesUseCase;
import garcias.api.catalog.category.application.usecases.category.CreateCategoryUseCase;
import garcias.api.catalog.category.application.usecases.category.DeleteCategoryUseCase;
import garcias.api.catalog.category.application.usecases.category.SearchCategoriesUseCase;
import garcias.api.catalog.category.application.usecases.category.UpdateCategoryUseCase;
import garcias.api.catalog.category.domain.entities.Category;
import garcias.api.catalog.category.domain.valueobjects.CategoryId;
import garcias.api.catalog.category.domain.valueobjects.CategoryName;
import garcias.api.catalog.category.infrastructure.presentation.admin.category.AdminCategoryController;
import garcias.api.catalog.category.infrastructure.presentation.admin.category.CategoryCreateController;
import garcias.api.catalog.category.infrastructure.presentation.admin.category.CategoryDeleteController;
import garcias.api.catalog.category.infrastructure.presentation.admin.category.CategoryUpdateController;
import garcias.api.catalog.category.infrastructure.presentation.web.category.PublicCategoryController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários dos Controllers de Categorias")
class CategoryControllersTest {

    private Category createSampleCategory(Long id, String name) {
        return new Category(new CategoryId(id), new CategoryName(name));
    }

    @Nested
    @DisplayName("AdminCategoryController")
    class AdminCategoryControllerTests {
        @Mock
        private SearchCategoriesUseCase searchCategoriesUseCase;

        @Test
        @DisplayName("Deve retornar lista não paginada quando parâmetros de paginação e filtro forem nulos")
        void shouldReturnUnpagedList() {
            AdminCategoryController controller = new AdminCategoryController(searchCategoriesUseCase);
            List<Category> list = List.of(createSampleCategory(1L, "Padaria"));
            when(searchCategoriesUseCase.execute()).thenReturn(list);

            ResponseEntity<?> response = controller.findAll(null, null, null);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isInstanceOf(List.class);
            List<?> body = (List<?>) response.getBody();
            assertThat(body).hasSize(1);
        }

        @Test
        @DisplayName("Deve retornar página de categorias quando paginação ou filtro forem informados")
        void shouldReturnPagedCategories() {
            AdminCategoryController controller = new AdminCategoryController(searchCategoriesUseCase);
            Page<Category> page = new PageImpl<>(List.of(createSampleCategory(1L, "Bebidas")));
            when(searchCategoriesUseCase.execute(eq("Beb"), any(Pageable.class))).thenReturn(page);

            ResponseEntity<?> response = controller.findAll(0, 10, "Beb");

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isInstanceOf(Page.class);
            Page<?> body = (Page<?>) response.getBody();
            assertThat(body.getTotalElements()).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("CategoryCreateController")
    class CategoryCreateControllerTests {
        @Mock
        private CreateCategoryUseCase createCategoryUseCase;

        @Test
        @DisplayName("Deve criar categoria e retornar 200/201")
        void shouldCreateCategory() {
            CategoryCreateController controller = new CategoryCreateController(createCategoryUseCase);
            CreateCategoryRequest request = new CreateCategoryRequest("Doces");
            Category created = createSampleCategory(10L, "Doces");
            when(createCategoryUseCase.execute(request)).thenReturn(created);

            ResponseEntity<CategoryAdmResponse> response = controller.create(request);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().name()).isEqualTo("Doces");
        }
    }

    @Nested
    @DisplayName("CategoryUpdateController")
    class CategoryUpdateControllerTests {
        @Mock
        private UpdateCategoryUseCase updateCategoryUseCase;

        @Test
        @DisplayName("Deve atualizar categoria existente com sucesso")
        void shouldUpdateCategory() {
            CategoryUpdateController controller = new CategoryUpdateController(updateCategoryUseCase);
            UpdateCategoryRequest request = new UpdateCategoryRequest("Carnes Nobres");
            Category updated = createSampleCategory(5L, "Carnes Nobres");
            when(updateCategoryUseCase.execute(eq(new CategoryId(5L)), eq(request))).thenReturn(updated);

            ResponseEntity<CategoryAdmResponse> response = controller.update(5L, request);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().name()).isEqualTo("Carnes Nobres");
        }
    }

    @Nested
    @DisplayName("CategoryDeleteController")
    class CategoryDeleteControllerTests {
        @Mock
        private DeleteCategoryUseCase deleteCategoryUseCase;
        @Mock
        private BatchDeleteCategoriesUseCase batchDeleteCategoriesUseCase;

        @Test
        @DisplayName("Deve excluir categoria por ID e retornar 204 No Content")
        void shouldDeleteCategoryById() {
            CategoryDeleteController controller = new CategoryDeleteController(deleteCategoryUseCase, batchDeleteCategoriesUseCase);

            ResponseEntity<Void> response = controller.delete(3L);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
            verify(deleteCategoryUseCase).execute(new CategoryId(3L));
        }

        @Test
        @DisplayName("Deve excluir categorias em lote e retornar 204 No Content")
        void shouldBatchDeleteCategories() {
            CategoryDeleteController controller = new CategoryDeleteController(deleteCategoryUseCase, batchDeleteCategoriesUseCase);
            BatchDeleteCategoriesRequest request = new BatchDeleteCategoriesRequest(List.of(1L, 2L, 3L));

            ResponseEntity<Void> response = controller.batchDelete(request);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
            verify(batchDeleteCategoriesUseCase).execute(request);
        }
    }

    @Nested
    @DisplayName("PublicCategoryController")
    class PublicCategoryControllerTests {
        @Mock
        private SearchCategoriesUseCase searchCategoriesUseCase;

        @Test
        @DisplayName("Deve retornar categorias públicas não paginadas quando filtros nulos")
        void shouldReturnUnpagedPublicCategories() {
            PublicCategoryController controller = new PublicCategoryController(searchCategoriesUseCase);
            List<Category> list = List.of(createSampleCategory(1L, "Padaria"));
            when(searchCategoriesUseCase.execute()).thenReturn(list);

            ResponseEntity<?> response = controller.findAll(null, null, null);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isInstanceOf(List.class);
        }

        @Test
        @DisplayName("Deve retornar página de categorias públicas quando filtros informados")
        void shouldReturnPagedPublicCategories() {
            PublicCategoryController controller = new PublicCategoryController(searchCategoriesUseCase);
            Page<Category> page = new PageImpl<>(List.of(createSampleCategory(1L, "Hortifruti")));
            when(searchCategoriesUseCase.execute(eq("Horti"), any(Pageable.class))).thenReturn(page);

            ResponseEntity<?> response = controller.findAll(0, 12, "Horti");

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isInstanceOf(Page.class);
        }
    }
}
