package garcias.api.content.application.services;

import garcias.api.content.application.usecases.SaveContentUseCase;
import garcias.api.content.domain.entities.SiteContent;
import garcias.api.content.domain.repositories.SiteContentRepository;
import garcias.api.shared.config.CacheNames;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

@Service
public class SaveContentService implements SaveContentUseCase {

    private final SiteContentRepository repository;

    public SaveContentService(SiteContentRepository repository) {
        this.repository = repository;
    }

    @Override
    @CacheEvict(value = CacheNames.HOME_CONTENT, allEntries = true)
    public SiteContent execute(SiteContent content) {
        return repository.save(content);
    }
}
