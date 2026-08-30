package garcias.api.content.application.usecases;

import garcias.api.content.domain.entities.SiteContent;
import java.util.Optional;

public interface GetContentUseCase {
    Optional<SiteContent> execute();
}
