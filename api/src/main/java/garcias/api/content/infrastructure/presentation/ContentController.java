package garcias.api.content.infrastructure.presentation;

import garcias.api.content.application.dto.SiteContentDto;
import garcias.api.content.application.usecases.GetContentUseCase;
import garcias.api.content.application.usecases.SaveContentUseCase;
import garcias.api.content.infrastructure.mapper.ContentDtoMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class ContentController {

    private final GetContentUseCase getUseCase;
    private final SaveContentUseCase saveUseCase;

    public ContentController(
            GetContentUseCase getUseCase,
            SaveContentUseCase saveUseCase
    ) {
        this.getUseCase = getUseCase;
        this.saveUseCase = saveUseCase;
    }

    @GetMapping("/api/public/content")
    public ResponseEntity<SiteContentDto> getPublicContent() {
        return getUseCase.execute()
                .map(ContentDtoMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    @GetMapping("/api/admin/content")
    public ResponseEntity<SiteContentDto> getAdminContent() {
        return getUseCase.execute()
                .map(ContentDtoMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    @PatchMapping("/api/admin/content")
    public ResponseEntity<SiteContentDto> patchAdminContent(
            @RequestBody SiteContentDto patch
    ) {
        var existing = getUseCase.execute().orElse(null);
        var merged = ContentDtoMapper.merge(existing, patch);
        var saved = saveUseCase.execute(merged);
        return ResponseEntity.ok(ContentDtoMapper.toDto(saved));
    }
}

