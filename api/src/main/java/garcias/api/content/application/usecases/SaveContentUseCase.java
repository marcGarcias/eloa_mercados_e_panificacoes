package garcias.api.content.application.usecases;

import garcias.api.content.domain.entities.SiteContent;

public interface SaveContentUseCase {
    SiteContent execute(SiteContent content);
}
