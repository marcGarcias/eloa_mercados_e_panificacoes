package garcias.api.content.infrastructure.mapper;

import garcias.api.content.domain.entities.SiteContent;
import garcias.api.content.domain.valueobjects.*;
import garcias.api.content.infrastructure.persistence.SiteContentJpaEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SiteContentMapper Unit Tests")
class SiteContentMapperTest {

    private final SiteContentMapper mapper = new SiteContentMapper();

    @Test
    @DisplayName("Deve serializar e desserializar SiteContent completo com sucesso")
    void testSerializationAndDeserialization() {
        Banner banner = new Banner("selo-banner", "titulo-banner", "sub", "desc", Collections.emptyList());
        Diferenciais dif = new Diferenciais("selo-dif", "titulo-dif", "desc", Collections.emptyList());
        Catalogo cat = new Catalogo("selo-cat", "desc");
        Sobre sobre = new Sobre("selo-sobre", "titulo-sobre", "desc", Collections.emptyList());
        Estatisticas est = new Estatisticas(Collections.emptyList());
        Cta cta = new Cta("selo-cta", "titulo-cta", "desc");
        Rodape rod = new Rodape("desc", "contato");
        Dados dados = new Dados("end", "9", "18", "seg-sex", "123", "123");

        SiteContent content = new SiteContent(banner, dif, cat, sobre, est, cta, rod, dados, Faq.defaultFaq());

        SiteContentJpaEntity entity = mapper.toJpa(content, 1L);
        assertNotNull(entity);
        assertEquals(1L, entity.getId());

        SiteContent deserialized = mapper.toDomain(entity);
        assertNotNull(deserialized);
        assertEquals("selo-banner", deserialized.banner().selo());
        assertEquals("end", deserialized.dados().endereco());
    }

    @Test
    @DisplayName("Deve recuperar objeto mesmo quando o banco contiver um array com OID e objeto")
    void shouldRecoverObjectFromArrayJson() {
        String legacyArrayJson = "[541033, {\"faq\": {\"itens\": []}}]";
        SiteContentJpaEntity entity = SiteContentJpaEntity.create(1L, legacyArrayJson);

        SiteContent result = mapper.toDomain(entity);
        assertNotNull(result);
        assertNotNull(result.faq());
    }

    @Test
    @DisplayName("Deve retornar null quando JPA entity ou data forem nulos ou vazios")
    void shouldReturnNullWhenEmptyOrNull() {
        assertNull(mapper.toDomain(null));
        assertNull(mapper.toDomain(SiteContentJpaEntity.create(1L, null)));
        assertNull(mapper.toDomain(SiteContentJpaEntity.create(1L, "   ")));
        assertNull(mapper.toDomain(SiteContentJpaEntity.create(1L, "12345")));
        assertNull(mapper.toDomain(SiteContentJpaEntity.create(1L, "[123, 456]")));
        assertNull(mapper.toJpa(null, 1L));
    }
}
