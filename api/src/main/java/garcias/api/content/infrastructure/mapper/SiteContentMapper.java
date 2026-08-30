package garcias.api.content.infrastructure.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import garcias.api.content.domain.entities.SiteContent;
import garcias.api.content.infrastructure.persistence.SiteContentJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class SiteContentMapper {

    private final ObjectMapper objectMapper;

    public SiteContentMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public SiteContent toDomain(SiteContentJpaEntity jpaEntity) {
        if (jpaEntity == null) return null;
        try {
            return objectMapper.readValue(jpaEntity.getData(), SiteContent.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Erro ao converter JSON do banco para domínio", e);
        }
    }

    public SiteContentJpaEntity toJpa(SiteContent domain, Long id) {
        if (domain == null) return null;
        try {
            String json = objectMapper.writeValueAsString(domain);
            return SiteContentJpaEntity.create(id, json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Erro ao converter domínio para JSON do banco", e);
        }
    }
}
