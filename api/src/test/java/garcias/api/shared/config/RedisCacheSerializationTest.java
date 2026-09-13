package garcias.api.shared.config;

import garcias.api.content.application.dto.SiteContentDto;
import garcias.api.content.domain.entities.SiteContent;
import garcias.api.content.infrastructure.mapper.ContentDtoMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RedisCacheSerializationTest {

    private final RedisSerializer<Object> serializer = RedisSerializer.json();

    @Test
    @DisplayName("Deve serializar e desserializar SiteContent populado com sucesso")
    void shouldSerializeAndDeserializePopulatedSiteContent() {
        SiteContentDto dto = new SiteContentDto();

        SiteContentDto.BannerDto banner = new SiteContentDto.BannerDto();
        banner.setSelo("Selo Banner");
        banner.setTitulo("Pães Artesanais");
        banner.setSubtitulo("Frescos todo dia");
        banner.setDescricao("A melhor padaria da cidade");
        SiteContentDto.IndicadorDto ind = new SiteContentDto.IndicadorDto();
        ind.setNome("Anos de tradição");
        ind.setValor("10+");
        banner.setIndicadores(List.of(ind));
        dto.setBanner(banner);

        SiteContentDto.CatalogoDto catalogo = new SiteContentDto.CatalogoDto();
        catalogo.setSelo("Nosso Catálogo");
        catalogo.setDescricao("Conheça nossa seleção de delícias");
        dto.setCatalogo(catalogo);

        SiteContentDto.DadosDto dados = new SiteContentDto.DadosDto();
        dados.setEndereco("Rua das Flores, 123");
        dados.setWhatsapp("(11) 99999-9999");
        dados.setCnpj("12.345.678/0001-90");
        dto.setDados(dados);

        SiteContent original = ContentDtoMapper.toDomain(dto);
        assertNotNull(original);

        byte[] bytes = serializer.serialize(original);
        assertNotNull(bytes);

        Object deserialized = serializer.deserialize(bytes);
        assertNotNull(deserialized);
        assertInstanceOf(SiteContent.class, deserialized);

        SiteContent restored = (SiteContent) deserialized;
        assertEquals(original.getBanner().titulo(), restored.getBanner().titulo());
        assertEquals(original.getCatalogo().selo(), restored.getCatalogo().selo());
        assertEquals(original.getDados().whatsapp(), restored.getDados().whatsapp());
    }

    @Test
    @DisplayName("Deve validar constantes e integridade de CacheNames")
    void shouldValidateCacheNames() throws Exception {
        assertEquals("home:content", CacheNames.HOME_CONTENT);
        assertEquals("home:categories", CacheNames.HOME_CATEGORIES);
        assertEquals("home:products", CacheNames.HOME_PRODUCTS);

        var constructor = CacheNames.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        Object instance = constructor.newInstance();
        assertNotNull(instance);
    }
}
