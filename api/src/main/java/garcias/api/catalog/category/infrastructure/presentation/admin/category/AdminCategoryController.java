package garcias.api.catalog.category.infrastructure.presentation.admin.category;

import garcias.api.catalog.category.application.dto.responses.CategoryAdmResponse;
import garcias.api.catalog.category.application.mapper.CategoryAdmResponseMapper;
import garcias.api.catalog.category.application.usecases.category.SearchCategoriesUseCase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<?> findAll(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String name
    ) {
        if (page != null || size != null || (name != null && !name.isBlank())) {
            int pageNumber = page != null ? page : 0;
            int pageSize = size != null ? size : 10;
            Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("name").ascending());
            Page<CategoryAdmResponse> pagedResult = searchCategoriesUseCase.execute(name, pageable)
                    .map(CategoryAdmResponseMapper::toResponse);
            return ResponseEntity.ok(pagedResult);
        }

        return ResponseEntity.ok(
                searchCategoriesUseCase.execute()
                        .stream()
                        .map(CategoryAdmResponseMapper::toResponse)
                        .toList()
        );
    }
}

