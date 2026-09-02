package garcias.api.content.infrastructure.presentation.admin.content;

import garcias.api.content.application.dto.SiteContentDto;
import garcias.api.content.application.usecases.GetContentUseCase;
import garcias.api.content.application.usecases.SaveContentUseCase;
import garcias.api.content.infrastructure.mapper.ContentDtoMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

@RestController
public class ContentAdminPatchController {

    private final GetContentUseCase getUseCase;
    private final SaveContentUseCase saveUseCase;

    public ContentAdminPatchController(
            GetContentUseCase getUseCase,
            SaveContentUseCase saveUseCase
    ) {
        this.getUseCase = getUseCase;
        this.saveUseCase = saveUseCase;
    }

    @PatchMapping("/api/admin/content")
    public ResponseEntity<SiteContentDto> patchAdminContent(
            @Valid @RequestBody SiteContentDto patch
    ) {
        var existing = getUseCase.execute().orElse(null);
        var merged = ContentDtoMapper.merge(existing, patch);
        var saved = saveUseCase.execute(merged);
        return ResponseEntity.ok(ContentDtoMapper.toDto(saved));
    }
}
