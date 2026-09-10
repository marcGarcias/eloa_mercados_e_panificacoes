package garcias.api.catalog.product.infrastructure.storage;

import garcias.api.catalog.product.application.storage.ImageStorage;
import garcias.api.catalog.product.domain.exceptions.ImageNotFoundException;
import garcias.api.catalog.product.infrastructure.exceptions.ImageStorageException;
import garcias.api.shared.exceptions.InvalidImageException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.UUID;

@Component
public class ImageStorageImpl implements ImageStorage {

    private static final long MAX_FILE_SIZE_BYTES = 5 * 1024 * 1024; // 5 MB
    private static final int MAX_WIDTH = 3840;
    private static final int MAX_HEIGHT = 2160;

    private final Path root;

    public ImageStorageImpl() {
        this.root = Paths.get("uploads/products").toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.root);
        } catch (IOException e) {
            throw new ImageStorageException("Não foi possível criar o diretório de uploads", e);
        }
    }

    @Override
    public String save(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidImageException("Arquivo de imagem obrigatório.");
        }

        if (file.getSize() > MAX_FILE_SIZE_BYTES) {
            throw new InvalidImageException("O tamanho da imagem excede o limite máximo de 5MB.");
        }

        try {
            Files.createDirectories(root);

            BufferedImage bufferedImage = decodeAndValidateImage(file);

            String filename = UUID.randomUUID() + ".webp";
            Path destination = root.resolve(filename).normalize().toAbsolutePath();

            if (!destination.startsWith(root)) {
                throw new ImageStorageException("Caminho de destino inválido.", null);
            }

            boolean written = ImageIO.write(bufferedImage, "webp", destination.toFile());
            if (!written) {
                throw new ImageStorageException("Falha ao codificar a imagem para o formato WebP.", null);
            }

            return "/uploads/products/" + filename;

        } catch (InvalidImageException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new ImageStorageException("Could not save image", exception);
        }
    }

    private BufferedImage decodeAndValidateImage(MultipartFile file) throws IOException {
        try (ImageInputStream input = ImageIO.createImageInputStream(file.getInputStream())) {
            if (input == null) {
                throw new InvalidImageException("Formato de imagem não reconhecido ou stream inválido.");
            }

            Iterator<ImageReader> readers = ImageIO.getImageReaders(input);
            if (!readers.hasNext()) {
                throw new InvalidImageException("Nenhum decodificador de imagem suportado para o arquivo fornecido.");
            }

            ImageReader reader = readers.next();
            try {
                reader.setInput(input, true, true);
                int width = reader.getWidth(0);
                int height = reader.getHeight(0);

                if (width <= 0 || height <= 0) {
                    throw new InvalidImageException("Dimensões inválidas da imagem.");
                }

                if (width > MAX_WIDTH || height > MAX_HEIGHT) {
                    throw new InvalidImageException(String.format("Dimensões da imagem (%dx%d) excedem o limite máximo permitido (%dx%d).",
                            width, height, MAX_WIDTH, MAX_HEIGHT));
                }

                BufferedImage image = reader.read(0);
                if (image == null) {
                    throw new InvalidImageException("Falha ao decodificar os dados da imagem.");
                }

                if (image.getType() == BufferedImage.TYPE_CUSTOM) {
                    BufferedImage converted = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
                    converted.getGraphics().drawImage(image, 0, 0, null);
                    return converted;
                }

                return image;
            } finally {
                reader.dispose();
            }
        }
    }

    @Override
    public void delete(String path) {
        if (path == null || path.isBlank()) {
            return;
        }

        try {
            String cleanPath = path.startsWith("/") ? path.substring(1) : path;
            Path file = Paths.get(cleanPath).normalize().toAbsolutePath();

            Path normalizedRoot = root.normalize().toAbsolutePath();
            if (!file.startsWith(normalizedRoot)) {
                return;
            }

            if (Files.exists(file)) {
                Path realFile = file.toRealPath();
                Path realRoot = normalizedRoot.toRealPath();
                if (!realFile.startsWith(realRoot)) {
                    return;
                }
                Files.deleteIfExists(realFile);
            }
        } catch (IOException exception) {
            throw new ImageStorageException("Could not delete image", exception);
        }
    }

    @Override
    public Resource load(String filename) {
        if (filename == null || filename.isBlank()) {
            throw new ImageNotFoundException();
        }

        try {
            Path normalizedRoot = root.normalize().toAbsolutePath();
            Path file = root.resolve(filename).normalize().toAbsolutePath();

            if (!file.startsWith(normalizedRoot)) {
                throw new ImageNotFoundException();
            }

            if (!Files.exists(file)) {
                throw new ImageNotFoundException();
            }

            Path realFile = file.toRealPath();
            Path realRoot = normalizedRoot.toRealPath();
            if (!realFile.startsWith(realRoot)) {
                throw new ImageNotFoundException();
            }

            if (!Files.isRegularFile(realFile) || !Files.isReadable(realFile)) {
                throw new ImageNotFoundException();
            }

            return new UrlResource(realFile.toUri());

        } catch (NoSuchFileException exception) {
            throw new ImageNotFoundException();
        } catch (IOException exception) {
            throw new ImageStorageException("Could not load image", exception);
        }
    }
}