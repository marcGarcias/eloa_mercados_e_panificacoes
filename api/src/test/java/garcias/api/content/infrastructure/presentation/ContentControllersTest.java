package garcias.api.content.infrastructure.presentation;

import garcias.api.content.application.dto.SiteContentDto;
import garcias.api.content.application.usecases.GetContentUseCase;
import garcias.api.content.application.usecases.SaveContentUseCase;
import garcias.api.content.domain.entities.SiteContent;
import garcias.api.content.domain.valueobjects.Banner;
import garcias.api.content.infrastructure.presentation.admin.content.ContentAdminGetController;
import garcias.api.content.infrastructure.presentation.admin.content.ContentAdminPatchController;
import garcias.api.content.infrastructure.presentation.web.content.ContentPublicGetController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários dos Controllers de Conteúdo do Site")
class ContentControllersTest {

    private SiteContent createSampleSiteContent(String bannerTitulo) {
        Banner banner = new Banner(null, bannerTitulo, null, null, null);
        return new SiteContent(banner, null, null, null, null, null, null, null, null);
    }

    @Nested
    @DisplayName("ContentPublicGetController")
    class ContentPublicGetControllerTests {
        @Mock
        private GetContentUseCase getContentUseCase;

        @Test
        @DisplayName("Deve retornar 200 OK com DTO quando existir conteúdo")
        void shouldReturn200WhenContentExists() {
            ContentPublicGetController controller = new ContentPublicGetController(getContentUseCase);
            when(getContentUseCase.execute()).thenReturn(Optional.of(createSampleSiteContent("Eloa Mercados")));

            ResponseEntity<SiteContentDto> response = controller.getPublicContent();

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getBanner().getTitulo()).isEqualTo("Eloa Mercados");
        }

        @Test
        @DisplayName("Deve retornar 204 No Content quando não houver conteúdo")
        void shouldReturn204WhenEmpty() {
            ContentPublicGetController controller = new ContentPublicGetController(getContentUseCase);
            when(getContentUseCase.execute()).thenReturn(Optional.empty());

            ResponseEntity<SiteContentDto> response = controller.getPublicContent();

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
            assertThat(response.getBody()).isNull();
        }
    }

    @Nested
    @DisplayName("ContentAdminGetController")
    class ContentAdminGetControllerTests {
        @Mock
        private GetContentUseCase getContentUseCase;

        @Test
        @DisplayName("Deve retornar 200 OK com DTO quando existir conteúdo para admin")
        void shouldReturn200WhenContentExists() {
            ContentAdminGetController controller = new ContentAdminGetController(getContentUseCase);
            when(getContentUseCase.execute()).thenReturn(Optional.of(createSampleSiteContent("Eloa Mercados")));

            ResponseEntity<SiteContentDto> response = controller.getAdminContent();

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getBanner().getTitulo()).isEqualTo("Eloa Mercados");
        }

        @Test
        @DisplayName("Deve retornar 204 No Content quando não houver conteúdo para admin")
        void shouldReturn204WhenEmpty() {
            ContentAdminGetController controller = new ContentAdminGetController(getContentUseCase);
            when(getContentUseCase.execute()).thenReturn(Optional.empty());

            ResponseEntity<SiteContentDto> response = controller.getAdminContent();

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
            assertThat(response.getBody()).isNull();
        }
    }

    @Nested
    @DisplayName("ContentAdminPatchController")
    class ContentAdminPatchControllerTests {
        @Mock
        private GetContentUseCase getContentUseCase;
        @Mock
        private SaveContentUseCase saveUseCase;

        @Test
        @DisplayName("Deve atualizar conteúdo com patch quando já existe conteúdo")
        void shouldPatchExistingContent() {
            ContentAdminPatchController controller = new ContentAdminPatchController(getContentUseCase, saveUseCase);
            SiteContent existing = createSampleSiteContent("Título Velho");
            when(getContentUseCase.execute()).thenReturn(Optional.of(existing));

            SiteContentDto patch = new SiteContentDto();
            SiteContentDto.BannerDto banner = new SiteContentDto.BannerDto();
            banner.setTitulo("Novo Título");
            patch.setBanner(banner);

            SiteContent saved = createSampleSiteContent("Novo Título");
            when(saveUseCase.execute(any(SiteContent.class))).thenReturn(saved);

            ResponseEntity<SiteContentDto> response = controller.patchAdminContent(patch);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getBanner().getTitulo()).isEqualTo("Novo Título");
            verify(saveUseCase).execute(any(SiteContent.class));
        }

        @Test
        @DisplayName("Deve criar conteúdo a partir do patch quando não existe conteúdo anterior")
        void shouldPatchWhenNoExistingContent() {
            ContentAdminPatchController controller = new ContentAdminPatchController(getContentUseCase, saveUseCase);
            when(getContentUseCase.execute()).thenReturn(Optional.empty());

            SiteContentDto patch = new SiteContentDto();
            SiteContentDto.BannerDto banner = new SiteContentDto.BannerDto();
            banner.setTitulo("Título Inicial");
            patch.setBanner(banner);

            SiteContent saved = createSampleSiteContent("Título Inicial");
            when(saveUseCase.execute(any(SiteContent.class))).thenReturn(saved);

            ResponseEntity<SiteContentDto> response = controller.patchAdminContent(patch);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            verify(saveUseCase).execute(any(SiteContent.class));
        }
    }
}
