package garcias.api.content.application.services;

import garcias.api.content.application.usecases.GetContentUseCase;
import garcias.api.content.domain.entities.SiteContent;
import garcias.api.content.domain.repositories.SiteContentRepository;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class GetContentService implements GetContentUseCase {

    private final SiteContentRepository repository;

    public GetContentService(SiteContentRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<SiteContent> execute() {
        return repository.find();
    }
}
