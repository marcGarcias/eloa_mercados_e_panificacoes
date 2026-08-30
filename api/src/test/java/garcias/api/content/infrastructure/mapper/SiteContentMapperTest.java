package garcias.api.content.infrastructure.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import garcias.api.content.domain.entities.SiteContent;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SiteContentMapperTest {

    @Test
    public void testSerializationAndDeserialization() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        
        SiteContent.Banner banner = new SiteContent.Banner("selo-banner", "titulo-banner", "sub", "desc", java.util.Collections.emptyList());
        SiteContent.Diferenciais dif = new SiteContent.Diferenciais("selo-dif", "titulo-dif", "desc", java.util.Collections.emptyList());
        SiteContent.Catalogo cat = new SiteContent.Catalogo("selo-cat", "desc");
        SiteContent.Sobre sobre = new SiteContent.Sobre("selo-sobre", "titulo-sobre", "desc", java.util.Collections.emptyList());
        SiteContent.Estatisticas est = new SiteContent.Estatisticas(java.util.Collections.emptyList());
        SiteContent.Cta cta = new SiteContent.Cta("selo-cta", "titulo-cta", "desc");
        SiteContent.Rodape rod = new SiteContent.Rodape("desc", "contato", "direitos");
        SiteContent.Dados dados = new SiteContent.Dados("end", "9", "18", "seg-sex", "123", "123");
        
        SiteContent content = new SiteContent(banner, dif, cat, sobre, est, cta, rod, dados);
        
        // 1. Serializa para JSON
        String json = objectMapper.writeValueAsString(content);
        assertNotNull(json);
        
        // 2. Desserializa de volta para o Domínio
        SiteContent deserialized = objectMapper.readValue(json, SiteContent.class);
        assertNotNull(deserialized);
        assertEquals("selo-banner", deserialized.banner().selo());
        assertEquals("end", deserialized.dados().endereco());
    }
}
