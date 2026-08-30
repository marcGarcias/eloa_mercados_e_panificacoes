package garcias.api.content.infrastructure.persistence;

import garcias.api.content.domain.entities.SiteContent;
import garcias.api.content.domain.repositories.SiteContentRepository;
import garcias.api.content.infrastructure.mapper.SiteContentMapper;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
public class SiteContentRepositoryImpl implements SiteContentRepository {

    private static final Long REGISTRATION_ID = 1L;

    private final SpringSiteContentJpaRepository jpaRepository;
    private final SiteContentMapper mapper;

    public SiteContentRepositoryImpl(
            SpringSiteContentJpaRepository jpaRepository,
            SiteContentMapper mapper
    ) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<SiteContent> find() {
        return jpaRepository.findById(REGISTRATION_ID)
                .map(mapper::toDomain);
    }

    @Override
    public SiteContent save(SiteContent content) {
        var existing = jpaRepository.findById(REGISTRATION_ID);
        SiteContentJpaEntity entityToSave;
        
        if (existing.isPresent()) {
            var entity = existing.get();
            var mapped = mapper.toJpa(content, REGISTRATION_ID);
            entity.setData(mapped.getData());
            entityToSave = entity;
        } else {
            entityToSave = mapper.toJpa(content, REGISTRATION_ID);
        }
        
        var saved = jpaRepository.save(entityToSave);
        return mapper.toDomain(saved);
    }
}
