package garcias.api.catalog.category.infrastructure.presentation.admin.category;



import garcias.api.catalog.category.application.dto.requests.BatchDeleteCategoriesRequest;
import garcias.api.catalog.category.application.usecases.category.BatchDeleteCategoriesUseCase;
import garcias.api.catalog.category.application.usecases.category.DeleteCategoryUseCase;
import garcias.api.catalog.category.domain.valueobjects.CategoryId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;


import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/admin/categories")
public class CategoryDeleteController {


    private final DeleteCategoryUseCase deleteCategoryUseCase;
    private final BatchDeleteCategoriesUseCase batchDeleteCategoriesUseCase;


    public CategoryDeleteController(
            DeleteCategoryUseCase deleteCategoryUseCase,
            BatchDeleteCategoriesUseCase batchDeleteCategoriesUseCase
    ) {
        this.deleteCategoryUseCase = deleteCategoryUseCase;
        this.batchDeleteCategoriesUseCase = batchDeleteCategoriesUseCase;
    }


    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete category"
    )
    @ApiResponse(
            responseCode = "204",
            description = "Category deleted successfully"
    )
    @ApiResponse(
            responseCode = "404",
            description = "Category not found"
    )
    public ResponseEntity<Void> delete(

            @PathVariable Long id

    ) {


        deleteCategoryUseCase.execute(
                new CategoryId(id)
        );


        return ResponseEntity.noContent().build();
    }

    @PostMapping("/batch-delete")
    @Operation(
            summary = "Delete categories in batch"
    )
    @ApiResponse(
            responseCode = "204",
            description = "Categories deleted successfully"
    )
    @ApiResponse(
            responseCode = "400",
            description = "Invalid request or empty category ID list"
    )
    public ResponseEntity<Void> batchDelete(
            @Valid
            @RequestBody
            BatchDeleteCategoriesRequest request
    ) {
        batchDeleteCategoriesUseCase.execute(request);
        return ResponseEntity.noContent().build();
    }
}