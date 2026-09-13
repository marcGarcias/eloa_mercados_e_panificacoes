package garcias.api.catalog.product.application.validation;

import garcias.api.shared.exceptions.InvalidImageException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("WebpImageValidator (Product Application) Unit Tests")
class WebpImageValidatorTest {

    private WebpImageValidator validator;

    @BeforeEach
    void setUp() {
        validator = new WebpImageValidator();
    }

    private byte[] createWebpBytes() {
        byte[] bytes = new byte[12];
        bytes[0] = 'R'; bytes[1] = 'I'; bytes[2] = 'F'; bytes[3] = 'F';
        bytes[4] = 0;   bytes[5] = 0;   bytes[6] = 0;   bytes[7] = 0;
        bytes[8] = 'W'; bytes[9] = 'E'; bytes[10] = 'B'; bytes[11] = 'P';
        return bytes;
    }

    @Test
    @DisplayName("Deve validar imagem WebP autêntica com sucesso")
    void shouldValidateAuthenticWebpImageSuccessfully() {
        MockMultipartFile file = new MockMultipartFile(
                "photo",
                "test.webp",
                "image/webp",
                createWebpBytes()
        );

        assertThatCode(() -> validator.validate(file)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Deve lançar InvalidImageException quando arquivo for nulo")
    void shouldThrowWhenFileIsNull() {
        assertThatThrownBy(() -> validator.validate(null))
                .isInstanceOf(InvalidImageException.class)
                .hasMessageContaining("Imagem obrigatória");
    }

    @Test
    @DisplayName("Deve lançar InvalidImageException quando arquivo for vazio")
    void shouldThrowWhenFileIsEmpty() {
        MockMultipartFile emptyFile = new MockMultipartFile(
                "photo", "empty.webp", "image/webp", new byte[0]
        );

        assertThatThrownBy(() -> validator.validate(emptyFile))
                .isInstanceOf(InvalidImageException.class)
                .hasMessageContaining("Imagem obrigatória");
    }

    @Test
    @DisplayName("Deve lançar InvalidImageException quando cabeçalho não for WebP")
    void shouldThrowWhenNotWebpHeader() {
        byte[] fakeJpg = new byte[]{(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0, 0, 0, 0, 0, 0, 0, 0, 0};
        MockMultipartFile file = new MockMultipartFile("photo", "fake.webp", "image/webp", fakeJpg);

        assertThatThrownBy(() -> validator.validate(file))
                .isInstanceOf(InvalidImageException.class)
                .hasMessageContaining("A imagem deve estar no formato WebP");
    }

    @Test
    @DisplayName("Deve lançar InvalidImageException quando cabeçalho tiver RIFF mas não WEBP")
    void shouldThrowWhenHeaderHasRiffButNotWebp() {
        byte[] riffNotWebp = new byte[]{'R', 'I', 'F', 'F', 0, 0, 0, 0, 'J', 'P', 'E', 'G'};
        MockMultipartFile file = new MockMultipartFile("photo", "fake.webp", "image/webp", riffNotWebp);

        assertThatThrownBy(() -> validator.validate(file))
                .isInstanceOf(InvalidImageException.class)
                .hasMessageContaining("A imagem deve estar no formato WebP");
    }

    @Test
    @DisplayName("Deve lançar InvalidImageException quando ocorrer IOException na leitura")
    void shouldThrowWhenIOExceptionOccurs() throws Exception {
        MultipartFile mockFile = mock(MultipartFile.class);
        InputStream mockStream = mock(InputStream.class);

        when(mockFile.isEmpty()).thenReturn(false);
        when(mockFile.getInputStream()).thenReturn(mockStream);
        when(mockStream.read(any(byte[].class))).thenThrow(new IOException("Disk error"));

        assertThatThrownBy(() -> validator.validate(mockFile))
                .isInstanceOf(InvalidImageException.class)
                .hasMessageContaining("A imagem deve estar no formato WebP");
    }
}
