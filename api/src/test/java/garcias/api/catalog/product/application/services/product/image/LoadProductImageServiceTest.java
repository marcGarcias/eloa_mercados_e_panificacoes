package garcias.api.catalog.product.application.services.product.image;

import garcias.api.catalog.product.application.storage.ImageStorage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("LoadProductImageService Unit Tests")
class LoadProductImageServiceTest {

    @Mock
    private ImageStorage imageStorage;

    @InjectMocks
    private LoadProductImageService loadProductImageService;

    @Test
    @DisplayName("Deve carregar recurso de imagem delegando para ImageStorage")
    void shouldLoadImageDelegatingToStorage() {
        Resource mockResource = mock(Resource.class);
        when(imageStorage.load("pao.webp")).thenReturn(mockResource);

        Resource result = loadProductImageService.execute("pao.webp");

        assertThat(result).isSameAs(mockResource);
    }
}
