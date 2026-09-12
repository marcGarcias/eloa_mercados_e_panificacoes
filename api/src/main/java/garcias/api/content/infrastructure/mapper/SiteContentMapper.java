package garcias.api.content.infrastructure.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import garcias.api.content.domain.entities.SiteContent;
import garcias.api.content.domain.valueobjects.Faq;
import garcias.api.content.infrastructure.persistence.SiteContentJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class SiteContentMapper {

    private final ObjectMapper objectMapper;

    public SiteContentMapper() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public SiteContent toDomain(SiteContentJpaEntity jpaEntity) {
        if (jpaEntity == null) return null;
        try {
            SiteContent content = objectMapper.readValue(jpaEntity.getData(), SiteContent.class);
            if (content != null && content.faq() == null) {
                // Fallback de segurança caso o registro JSON legado não possua a chave "faq"
                return new SiteContent(
                        content.banner(),
                        content.diferenciais(),
                        content.catalogo(),
                        content.sobre(),
                        content.estatisticas(),
                        content.cta(),
                        content.rodape(),
                        content.dados(),
                        Faq.defaultFaq()
                );
            }
            return content;
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
