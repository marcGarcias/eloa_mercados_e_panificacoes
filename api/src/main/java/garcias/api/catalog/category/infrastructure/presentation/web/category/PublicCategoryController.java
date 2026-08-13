package garcias.api.catalog.category.infrastructure.presentation.web.category;

import garcias.api.catalog.category.application.dto.responses.CategoryWebResponse;
import garcias.api.catalog.category.application.mapper.CategoryWebResponseMapper;
import garcias.api.catalog.category.application.usecases.category.SearchCategoriesUseCase;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;



@RestController
@RequestMapping("/api/public/categories")
public class PublicCategoryController {


    private final SearchCategoriesUseCase searchCategoriesUseCase;


    public PublicCategoryController(
            SearchCategoriesUseCase searchCategoriesUseCase
    ) {
        this.searchCategoriesUseCase = searchCategoriesUseCase;
    }



    @GetMapping
    public ResponseEntity<List<CategoryWebResponse>> findAll() {


        return ResponseEntity.ok(
                searchCategoriesUseCase.execute()
                        .stream()
                        .map(CategoryWebResponseMapper::toResponse)
                        .toList()
        );
    }
}