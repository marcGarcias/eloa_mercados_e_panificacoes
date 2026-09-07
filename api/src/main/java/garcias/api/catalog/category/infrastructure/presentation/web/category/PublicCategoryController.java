package garcias.api.catalog.category.infrastructure.presentation.web.category;

import garcias.api.catalog.category.application.dto.responses.CategoryWebResponse;
import garcias.api.catalog.category.application.mapper.CategoryWebResponseMapper;
import garcias.api.catalog.category.application.usecases.category.SearchCategoriesUseCase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    public ResponseEntity<?> findAll(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String name
    ) {
        if (page != null || size != null || (name != null && !name.isBlank())) {
            int pageNumber = page != null ? page : 0;
            int pageSize = size != null ? size : 12;
            Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("name").ascending());
            Page<CategoryWebResponse> pagedResult = searchCategoriesUseCase.execute(name, pageable)
                    .map(CategoryWebResponseMapper::toResponse);
            return ResponseEntity.ok(pagedResult);
        }

        return ResponseEntity.ok(
                searchCategoriesUseCase.execute()
                        .stream()
                        .map(CategoryWebResponseMapper::toResponse)
                        .toList()
        );
    }
}