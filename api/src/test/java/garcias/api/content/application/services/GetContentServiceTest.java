package garcias.api.content.application.services;

import garcias.api.content.domain.entities.SiteContent;
import garcias.api.content.domain.repositories.SiteContentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetContentService Unit Tests")
class GetContentServiceTest {

    @Mock
    private SiteContentRepository repository;

    @InjectMocks
    private GetContentService getContentService;

    @Test
    @DisplayName("Deve retornar conteúdo quando presente no repositório")
    void shouldReturnContentWhenPresent() {
        SiteContent mockContent = mock(SiteContent.class);
        when(repository.find()).thenReturn(Optional.of(mockContent));

        Optional<SiteContent> result = getContentService.execute();

        assertThat(result).isPresent().contains(mockContent);
    }

    @Test
    @DisplayName("Deve retornar Optional vazio quando não houver conteúdo")
    void shouldReturnEmptyWhenNoContent() {
        when(repository.find()).thenReturn(Optional.empty());

        Optional<SiteContent> result = getContentService.execute();

        assertThat(result).isEmpty();
    }
}
