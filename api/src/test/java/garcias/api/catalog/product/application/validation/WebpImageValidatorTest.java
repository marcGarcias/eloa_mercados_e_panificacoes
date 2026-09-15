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

    private byte[] createPngBytes() {
        return new byte[]{(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A, 0, 0, 0, 0};
    }

    private byte[] createJpegBytes() {
        return new byte[]{(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0, 0, 0, 0, 0, 0, 0, 0, 0};
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
    @DisplayName("Deve validar imagem PNG autêntica com sucesso")
    void shouldValidateAuthenticPngImageSuccessfully() {
        MockMultipartFile file = new MockMultipartFile(
                "photo",
                "test.png",
                "image/png",
                createPngBytes()
        );

        assertThatCode(() -> validator.validate(file)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Deve validar imagem JPEG autêntica com sucesso")
    void shouldValidateAuthenticJpegImageSuccessfully() {
        MockMultipartFile file = new MockMultipartFile(
                "photo",
                "test.jpg",
                "image/jpeg",
                createJpegBytes()
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
    @DisplayName("Deve lançar InvalidImageException quando arquivo ultrapassar 10MB")
    void shouldThrowWhenFileExceeds10MB() {
        byte[] oversizedBytes = new byte[11 * 1024 * 1024];
        MockMultipartFile oversizedFile = new MockMultipartFile(
                "photo", "oversized.webp", "image/webp", oversizedBytes
        );

        assertThatThrownBy(() -> validator.validate(oversizedFile))
                .isInstanceOf(InvalidImageException.class)
                .hasMessageContaining("10MB");
    }

    @Test
    @DisplayName("Deve lançar InvalidImageException quando cabeçalho não for formato de imagem suportado")
    void shouldThrowWhenNotSupportedHeader() {
        byte[] fakeBytes = "<?php echo 'script'; ?>".getBytes();
        MockMultipartFile file = new MockMultipartFile("photo", "fake.webp", "image/webp", fakeBytes);

        assertThatThrownBy(() -> validator.validate(file))
                .isInstanceOf(InvalidImageException.class)
                .hasMessageContaining("Formato de imagem não suportado");
    }

    @Test
    @DisplayName("Deve lançar InvalidImageException quando cabeçalho tiver RIFF mas não WEBP")
    void shouldThrowWhenHeaderHasRiffButNotWebp() {
        byte[] riffNotWebp = new byte[]{'R', 'I', 'F', 'F', 0, 0, 0, 0, 'A', 'V', 'I', ' '};
        MockMultipartFile file = new MockMultipartFile("photo", "fake.webp", "image/webp", riffNotWebp);

        assertThatThrownBy(() -> validator.validate(file))
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
        when(mockStream.read(any(byte[].class))).thenThrow(new IOException("Disk error"));

        assertThatThrownBy(() -> validator.validate(mockFile))
                .isInstanceOf(InvalidImageException.class)
                .hasMessageContaining("Formato de imagem não suportado");
    }
}
