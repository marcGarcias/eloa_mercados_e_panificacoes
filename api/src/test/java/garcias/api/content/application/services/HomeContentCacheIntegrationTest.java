package garcias.api.content.application.services;

import garcias.api.content.application.usecases.GetContentUseCase;
import garcias.api.content.application.usecases.SaveContentUseCase;
import garcias.api.content.domain.entities.SiteContent;
import garcias.api.content.domain.repositories.SiteContentRepository;
import garcias.api.shared.config.CacheNames;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringJUnitConfig(HomeContentCacheIntegrationTest.TestCacheConfig.class)
@DisplayName("Home Content Cache Integration Tests")
class HomeContentCacheIntegrationTest {

    @Configuration
    @EnableCaching(proxyTargetClass = true)
    static class TestCacheConfig {

        @Bean
        public CacheManager cacheManager() {
            return new ConcurrentMapCacheManager(CacheNames.HOME_CONTENT);
        }

        @Bean
        public SiteContentRepository siteContentRepository() {
            return mock(SiteContentRepository.class);
        }

        @Bean
        public GetContentUseCase getContentService(SiteContentRepository repository) {
            return new GetContentService(repository);
        }

        @Bean
        public SaveContentUseCase saveContentService(SiteContentRepository repository) {
            return new SaveContentService(repository);
        }
    }

    @Autowired
    private GetContentUseCase getContentUseCase;

    @Autowired
    private SaveContentUseCase saveContentUseCase;

    @Autowired
    private SiteContentRepository repository;

    @Autowired
    private CacheManager cacheManager;

    @Test
    @DisplayName("Deve consultar o repositório na primeira chamada, usar cache na segunda e invalidar após salvar no admin")
    void shouldCacheAndEvictOnSave() {
        SiteContent mockContent = mock(SiteContent.class);
        when(repository.find()).thenReturn(Optional.of(mockContent));
        when(repository.save(any())).thenReturn(mockContent);

        // 1. Primeira chamada: deve consultar o repositório
        Optional<SiteContent> firstResult = getContentUseCase.execute();
        assertThat(firstResult).isPresent().contains(mockContent);
        verify(repository, times(1)).find();

        // 2. Segunda chamada: deve retornar do cache sem chamar o repositório novamente
        Optional<SiteContent> secondResult = getContentUseCase.execute();
        assertThat(secondResult).isPresent().contains(mockContent);
        verify(repository, times(1)).find();

        // Verifica que o conteúdo está armazenado no cache
        var cache = cacheManager.getCache(CacheNames.HOME_CONTENT);
        assertThat(cache).isNotNull();
        assertThat(cache.get("site-content")).isNotNull();

        // 3. Admin salva alterações: deve invalidar o cache da Home
        saveContentUseCase.execute(mockContent);
        verify(repository, times(1)).save(mockContent);
        assertThat(cache.get("site-content")).isNull();

        // 4. Terceira chamada: cache miss, deve consultar o repositório novamente
        Optional<SiteContent> thirdResult = getContentUseCase.execute();
        assertThat(thirdResult).isPresent().contains(mockContent);
        verify(repository, times(2)).find();
    }
}
