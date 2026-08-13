package garcias.api.catalog.category.infrastructure.presentation.admin.category;



import garcias.api.catalog.category.application.dto.requests.CreateCategoryRequest;
import garcias.api.catalog.category.application.dto.responses.CategoryAdmResponse;
import garcias.api.catalog.category.application.mapper.CategoryAdmResponseMapper;
import garcias.api.catalog.category.application.usecases.category.CreateCategoryUseCase;
import garcias.api.catalog.category.domain.entities.Category;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;


import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/admin/categories")
public class CategoryCreateController {


    private final CreateCategoryUseCase createCategoryUseCase;


    public CategoryCreateController(
            CreateCategoryUseCase createCategoryUseCase
    ) {
        this.createCategoryUseCase = createCategoryUseCase;
    }


    @PostMapping
    @Operation(
            summary = "Create category"
    )
    @ApiResponse(
            responseCode = "201",
            description = "Category created successfully"
    )
    @ApiResponse(
            responseCode = "400",
            description = "Invalid category data"
    )
    public ResponseEntity<CategoryAdmResponse> create(

            @Valid
            @RequestBody
            CreateCategoryRequest request

    ) {


        Category category =
                createCategoryUseCase.execute(request);


        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        CategoryAdmResponseMapper.toResponse(category)
                );
    }
}