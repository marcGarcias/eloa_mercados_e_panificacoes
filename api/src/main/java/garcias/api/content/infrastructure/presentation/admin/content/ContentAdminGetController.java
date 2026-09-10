package garcias.api.content.infrastructure.presentation.admin.content;

import garcias.api.content.application.dto.SiteContentDto;
import garcias.api.content.application.usecases.GetContentUseCase;
import garcias.api.content.infrastructure.mapper.ContentDtoMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ContentAdminGetController {

    private final GetContentUseCase getUseCase;

    public ContentAdminGetController(GetContentUseCase getUseCase) {
        this.getUseCase = getUseCase;
    }

    @GetMapping("/api/admin/content")
    public ResponseEntity<SiteContentDto> getAdminContent() {
        return getUseCase.execute()
                .map(ContentDtoMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }
}
