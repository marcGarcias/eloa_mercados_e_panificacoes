package garcias.api.shared.validations;

import garcias.api.shared.exceptions.InvalidImageException;
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

@DisplayName("WebpImageValidator (Shared) Unit Tests")
class WebpImageValidatorSharedTest {

    private byte[] createWebpBytes() {
        byte[] bytes = new byte[12];
        bytes[0] = 'R'; bytes[1] = 'I'; bytes[2] = 'F'; bytes[3] = 'F';
        bytes[4] = 0;   bytes[5] = 0;   bytes[6] = 0;   bytes[7] = 0;
        bytes[8] = 'W'; bytes[9] = 'E'; bytes[10] = 'B'; bytes[11] = 'P';
        return bytes;
    }

    private byte[] createPngBytes() {
        return new byte[]{(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A, 0, 0, 0, 0};
    }

    private byte[] createJpegBytes() {
        return new byte[]{(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0, 0, 0, 0, 0, 0, 0, 0, 0};
    }

    @Test
    @DisplayName("Deve validar imagem WebP autêntica sem lançar exceção")
    void shouldValidateValidWebpImage() {
        MockMultipartFile file = new MockMultipartFile(
                "image", "foto.webp", "image/webp", createWebpBytes()
        );

        assertThatCode(() -> WebpImageValidator.validate(file)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Deve validar imagem PNG autêntica sem lançar exceção")
    void shouldValidateValidPngImage() {
        MockMultipartFile file = new MockMultipartFile(
                "image", "foto.png", "image/png", createPngBytes()
        );

        assertThatCode(() -> WebpImageValidator.validate(file)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Deve validar imagem JPEG autêntica sem lançar exceção")
    void shouldValidateValidJpegImage() {
        MockMultipartFile file = new MockMultipartFile(
                "image", "foto.jpg", "image/jpeg", createJpegBytes()
        );

        assertThatCode(() -> WebpImageValidator.validate(file)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Deve lançar InvalidImageException quando arquivo for nulo")
    void shouldThrowWhenFileIsNull() {
        assertThatThrownBy(() -> WebpImageValidator.validate(null))
                .isInstanceOf(InvalidImageException.class)
                .hasMessageContaining("Imagem obrigatória");
    }

    @Test
    @DisplayName("Deve lançar InvalidImageException quando arquivo for vazio")
    void shouldThrowWhenFileIsEmpty() {
        MockMultipartFile emptyFile = new MockMultipartFile(
                "image", "vazio.webp", "image/webp", new byte[0]
        );

        assertThatThrownBy(() -> WebpImageValidator.validate(emptyFile))
                .isInstanceOf(InvalidImageException.class)
                .hasMessageContaining("Imagem obrigatória");
    }

    @Test
    @DisplayName("Deve lançar InvalidImageException quando arquivo ultrapassar 10MB")
    void shouldThrowWhenFileExceeds10MB() {
        byte[] oversizedBytes = new byte[11 * 1024 * 1024];
        MockMultipartFile oversizedFile = new MockMultipartFile(
                "image", "grande.webp", "image/webp", oversizedBytes
        );

        assertThatThrownBy(() -> WebpImageValidator.validate(oversizedFile))
                .isInstanceOf(InvalidImageException.class)
                .hasMessageContaining("10MB");
    }

    @Test
    @DisplayName("Deve lançar InvalidImageException quando cabeçalho não for imagem suportada")
    void shouldThrowWhenHeaderIsNotSupported() {
        byte[] notImage = new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};
        MockMultipartFile file = new MockMultipartFile(
                "image", "foto.exe", "application/octet-stream", notImage
        );

        assertThatThrownBy(() -> WebpImageValidator.validate(file))
                .isInstanceOf(InvalidImageException.class)
                .hasMessageContaining("Formato de imagem não suportado");
    }

    @Test
    @DisplayName("Deve lançar InvalidImageException quando ocorrer IOException na leitura")
    void shouldThrowWhenIOExceptionOccurs() throws Exception {
        MultipartFile mockFile = mock(MultipartFile.class);
        InputStream mockStream = mock(InputStream.class);

        when(mockFile.isEmpty()).thenReturn(false);
        when(mockFile.getInputStream()).thenReturn(mockStream);
        when(mockStream.read(any(byte[].class))).thenThrow(new IOException("Read error"));

        assertThatThrownBy(() -> WebpImageValidator.validate(mockFile))
                .isInstanceOf(InvalidImageException.class)
                .hasMessageContaining("Formato de imagem não suportado");
    }
}
