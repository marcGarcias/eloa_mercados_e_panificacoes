package garcias.api.catalog.product.infrastructure.storage;

import garcias.api.catalog.product.domain.exceptions.ImageNotFoundException;
import garcias.api.shared.exceptions.InvalidImageException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ImageStorageImpl Security and Responsive Variants Unit Tests")
class ImageStorageImplTest {

    @TempDir
    Path tempDir;

    private ImageStorageImpl imageStorage;

    @BeforeEach
    void setUp() {
        imageStorage = new ImageStorageImpl();
        ReflectionTestUtils.setField(imageStorage, "root", tempDir);
    }

    private byte[] createSampleImageBytes(int width, int height, String format) throws IOException {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, format, baos);
        return baos.toByteArray();
    }

    @Test
    @DisplayName("Should successfully save valid PNG image generating all responsive WebP variants")
    void shouldSaveValidImageSuccessfullyAndGenerateVariants() throws IOException {
        byte[] validPng = createSampleImageBytes(300, 300, "png");
        MockMultipartFile file = new MockMultipartFile("image", "photo.png", "image/png", validPng);

        String path = imageStorage.save(file);

        assertNotNull(path);
        assertTrue(path.endsWith(".webp"));
        assertTrue(path.startsWith("/uploads/products/"));

        String filename = path.substring(path.lastIndexOf('/') + 1);
        String uuid = filename.replace(".webp", "");

        assertTrue(Files.exists(tempDir.resolve(uuid + ".webp")));
        assertTrue(Files.exists(tempDir.resolve(uuid + "-lg.webp")));
        assertTrue(Files.exists(tempDir.resolve(uuid + "-md.webp")));
        assertTrue(Files.exists(tempDir.resolve(uuid + "-sm.webp")));
    }

    @Test
    @DisplayName("Should successfully save valid JPEG and WebP images")
    void shouldSaveJpegAndWebpImagesSuccessfully() throws IOException {
        byte[] validJpg = createSampleImageBytes(200, 200, "jpeg");
        MockMultipartFile jpgFile = new MockMultipartFile("image", "photo.jpg", "image/jpeg", validJpg);

        String pathJpg = imageStorage.save(jpgFile);
        assertNotNull(pathJpg);

        byte[] validWebp = createSampleImageBytes(200, 200, "png"); // encode as png, decode and convert to webp
        MockMultipartFile webpFile = new MockMultipartFile("image", "photo.webp", "image/webp", validWebp);

        String pathWebp = imageStorage.save(webpFile);
        assertNotNull(pathWebp);
    }

    @Test
    @DisplayName("Should delete all responsive variants when delete is called")
    void shouldDeleteAllVariantsOnDelete() throws IOException {
        byte[] validPng = createSampleImageBytes(200, 200, "png");
        MockMultipartFile file = new MockMultipartFile("image", "photo.png", "image/png", validPng);

        String path = imageStorage.save(file);
        String filename = path.substring(path.lastIndexOf('/') + 1);
        String uuid = filename.replace(".webp", "");

        assertTrue(Files.exists(tempDir.resolve(uuid + ".webp")));
        assertTrue(Files.exists(tempDir.resolve(uuid + "-lg.webp")));
        assertTrue(Files.exists(tempDir.resolve(uuid + "-md.webp")));
        assertTrue(Files.exists(tempDir.resolve(uuid + "-sm.webp")));

        imageStorage.delete(path);

        assertFalse(Files.exists(tempDir.resolve(uuid + ".webp")));
        assertFalse(Files.exists(tempDir.resolve(uuid + "-lg.webp")));
        assertFalse(Files.exists(tempDir.resolve(uuid + "-md.webp")));
        assertFalse(Files.exists(tempDir.resolve(uuid + "-sm.webp")));
    }

    @Test
    @DisplayName("Should throw InvalidImageException when file is null or empty")
    void shouldRejectEmptyFile() {
        MockMultipartFile emptyFile = new MockMultipartFile("image", "empty.png", "image/png", new byte[0]);

        assertThrows(InvalidImageException.class, () -> imageStorage.save(null));
        assertThrows(InvalidImageException.class, () -> imageStorage.save(emptyFile));
    }

    @Test
    @DisplayName("Should throw InvalidImageException when file exceeds 10MB limit")
    void shouldRejectFileExceedingSizeLimit() {
        byte[] oversizedBytes = new byte[11 * 1024 * 1024];
        MockMultipartFile file = new MockMultipartFile("image", "large.png", "image/png", oversizedBytes);

        InvalidImageException ex = assertThrows(InvalidImageException.class, () -> imageStorage.save(file));
        assertTrue(ex.getMessage().contains("10MB"));
    }

    @Test
    @DisplayName("Should throw InvalidImageException when non-image or script is uploaded (Fake extension)")
    void shouldRejectNonImageContent() {
        byte[] scriptBytes = "<?php echo 'malicious code'; ?>".getBytes();
        MockMultipartFile file = new MockMultipartFile("image", "shell.php.png", "image/png", scriptBytes);

        assertThrows(InvalidImageException.class, () -> imageStorage.save(file));
    }

    @Test
    @DisplayName("Should throw InvalidImageException when image dimensions exceed 3840x2160 (Decompression Bomb)")
    void shouldRejectDecompressionBombDimensions() throws IOException {
        byte[] hugeDimensionImage = createSampleImageBytes(4000, 100, "png");
        MockMultipartFile file = new MockMultipartFile("image", "bomb.png", "image/png", hugeDimensionImage);

        InvalidImageException ex = assertThrows(InvalidImageException.class, () -> imageStorage.save(file));
        assertTrue(ex.getMessage().contains("excedem o limite máximo permitido"));
    }

    @Test
    @DisplayName("Should prevent Path Traversal attacks when loading resources")
    void shouldPreventPathTraversalOnLoad() {
        assertThrows(ImageNotFoundException.class, () -> imageStorage.load("../../../pom.xml"));
        assertThrows(ImageNotFoundException.class, () -> imageStorage.load("..\\..\\..\\pom.xml"));
        assertThrows(ImageNotFoundException.class, () -> imageStorage.load(null));
        assertThrows(ImageNotFoundException.class, () -> imageStorage.load("    "));
    }

    @Test
    @DisplayName("Should throw ImageNotFoundException when file does not exist")
    void shouldThrowWhenFileNotFound() {
        assertThrows(ImageNotFoundException.class, () -> imageStorage.load("non-existent-image.webp"));
    }
}

