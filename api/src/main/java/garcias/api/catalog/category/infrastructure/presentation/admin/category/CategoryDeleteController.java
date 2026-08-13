package garcias.api.catalog.category.infrastructure.presentation.admin.category;



import garcias.api.catalog.category.application.usecases.category.DeleteCategoryUseCase;
import garcias.api.catalog.category.domain.valueobjects.CategoryId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/admin/categories")
public class CategoryDeleteController {


    private final DeleteCategoryUseCase deleteCategoryUseCase;


    public CategoryDeleteController(
            DeleteCategoryUseCase deleteCategoryUseCase
    ) {
        this.deleteCategoryUseCase = deleteCategoryUseCase;
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
}