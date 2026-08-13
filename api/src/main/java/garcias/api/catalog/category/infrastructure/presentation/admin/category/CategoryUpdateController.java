package garcias.api.catalog.category.infrastructure.presentation.admin.category;

import garcias.api.catalog.category.application.dto.requests.UpdateCategoryRequest;
import garcias.api.catalog.category.application.dto.responses.CategoryAdmResponse;
import garcias.api.catalog.category.application.mapper.CategoryAdmResponseMapper;
import garcias.api.catalog.category.application.usecases.category.UpdateCategoryUseCase;
import garcias.api.catalog.category.domain.entities.Category;
import garcias.api.catalog.category.domain.valueobjects.CategoryId;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/admin/categories")
public class CategoryUpdateController {


    private final UpdateCategoryUseCase updateCategoryUseCase;


    public CategoryUpdateController(
            UpdateCategoryUseCase updateCategoryUseCase
    ) {
        this.updateCategoryUseCase = updateCategoryUseCase;
    }


    @PutMapping("/{id}")
    @Operation(
            summary = "Update category",
            description = "Updates an existing product category."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Category updated successfully",
            content = @Content(
                    schema = @Schema(
                            implementation = CategoryAdmResponse.class
                    )
            )
    )
    @ApiResponse(
            responseCode = "404",
            description = "Category not found"
    )
    @ApiResponse(
            responseCode = "409",
            description = "Category already exists"
    )
    public ResponseEntity<CategoryAdmResponse> update(

            @PathVariable Long id,

            @Valid
            @RequestBody
            UpdateCategoryRequest request

    ) {


        Category category =
                updateCategoryUseCase.execute(
                        new CategoryId(id),
                        request
                );


        return ResponseEntity.ok(
                CategoryAdmResponseMapper.toResponse(category)
        );
    }
}