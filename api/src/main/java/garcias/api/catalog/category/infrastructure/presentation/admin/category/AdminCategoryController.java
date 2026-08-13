package garcias.api.catalog.category.infrastructure.presentation.admin.category;

import garcias.api.catalog.category.application.dto.responses.CategoryAdmResponse;
import garcias.api.catalog.category.application.mapper.CategoryAdmResponseMapper;
import garcias.api.catalog.category.application.usecases.category.SearchCategoriesUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/categories")
public class AdminCategoryController {

    private final SearchCategoriesUseCase searchCategoriesUseCase;


    public AdminCategoryController(
            SearchCategoriesUseCase searchCategoriesUseCase
    ){
        this.searchCategoriesUseCase = searchCategoriesUseCase;
    }


    @GetMapping
    public ResponseEntity<List<CategoryAdmResponse>> findAll() {


        return ResponseEntity.ok(
                searchCategoriesUseCase.execute()
                        .stream()
                        .map(CategoryAdmResponseMapper::toResponse)
                        .toList()
        );
    }
}
