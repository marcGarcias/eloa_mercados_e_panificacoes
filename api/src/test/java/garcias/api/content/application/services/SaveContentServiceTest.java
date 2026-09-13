package garcias.api.content.application.services;

import garcias.api.content.domain.entities.SiteContent;
import garcias.api.content.domain.repositories.SiteContentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("SaveContentService Unit Tests")
class SaveContentServiceTest {

    @Mock
    private SiteContentRepository repository;

    @InjectMocks
    private SaveContentService saveContentService;

    @Test
    @DisplayName("Deve salvar e retornar o conteúdo através do repositório")
    void shouldSaveAndReturnContent() {
        SiteContent content = mock(SiteContent.class);
        when(repository.save(content)).thenReturn(content);

        SiteContent result = saveContentService.execute(content);

        assertThat(result).isSameAs(content);
    }
}
