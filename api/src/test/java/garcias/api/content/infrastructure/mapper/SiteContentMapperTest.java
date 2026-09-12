package garcias.api.content.infrastructure.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import garcias.api.content.domain.entities.SiteContent;
import garcias.api.content.domain.valueobjects.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SiteContentMapperTest {

    @Test
    public void testSerializationAndDeserialization() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        
        Banner banner = new Banner("selo-banner", "titulo-banner", "sub", "desc", java.util.Collections.emptyList());
        Diferenciais dif = new Diferenciais("selo-dif", "titulo-dif", "desc", java.util.Collections.emptyList());
        Catalogo cat = new Catalogo("selo-cat", "desc");
        Sobre sobre = new Sobre("selo-sobre", "titulo-sobre", "desc", java.util.Collections.emptyList());
        Estatisticas est = new Estatisticas(java.util.Collections.emptyList());
        Cta cta = new Cta("selo-cta", "titulo-cta", "desc");
        Rodape rod = new Rodape("desc", "contato", "direitos");
        Dados dados = new Dados("end", "9", "18", "seg-sex", "123", "123");
        
        SiteContent content = new SiteContent(banner, dif, cat, sobre, est, cta, rod, dados, Faq.defaultFaq());
        
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
