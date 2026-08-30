package garcias.api.content.domain.repositories;

import garcias.api.content.domain.entities.SiteContent;
import java.util.Optional;

public interface SiteContentRepository {
    Optional<SiteContent> find();
    SiteContent save(SiteContent content);
}
