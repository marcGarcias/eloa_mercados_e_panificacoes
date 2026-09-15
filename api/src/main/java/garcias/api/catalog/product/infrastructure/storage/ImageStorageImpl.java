package garcias.api.catalog.product.infrastructure.storage;

import garcias.api.catalog.product.application.storage.ImageStorage;
import garcias.api.catalog.product.domain.exceptions.ImageNotFoundException;
import garcias.api.catalog.product.infrastructure.exceptions.ImageStorageException;
import garcias.api.shared.exceptions.InvalidImageException;
import net.coobird.thumbnailator.Thumbnails;
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

    private static final long MAX_FILE_SIZE_BYTES = 10 * 1024 * 1024; // 10MB
    private static final int MAX_WIDTH = 3840;
    private static final int MAX_HEIGHT = 2160;

    private static final int LARGE_DIMENSION = 1080;
    private static final int MEDIUM_DIMENSION = 500;
    private static final int SMALL_DIMENSION = 200;

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
            throw new InvalidImageException("A imagem não pode ultrapassar o limite máximo de 10MB.");
        }

        try {
            Files.createDirectories(root);

            BufferedImage bufferedImage = decodeAndValidateImage(file);

            String uuid = UUID.randomUUID().toString();
            String baseFilename = uuid + ".webp";
            String lgFilename = uuid + "-lg.webp";
            String mdFilename = uuid + "-md.webp";
            String smFilename = uuid + "-sm.webp";

            Path baseDestination = root.resolve(baseFilename).normalize().toAbsolutePath();
            Path lgDestination = root.resolve(lgFilename).normalize().toAbsolutePath();
            Path mdDestination = root.resolve(mdFilename).normalize().toAbsolutePath();
            Path smDestination = root.resolve(smFilename).normalize().toAbsolutePath();

            if (!baseDestination.startsWith(root) || !lgDestination.startsWith(root)
                    || !mdDestination.startsWith(root) || !smDestination.startsWith(root)) {
                throw new ImageStorageException("Caminho de destino inválido.", null);
            }

            // Geração de Variante Large (máx 1080x1080, qualidade 0.82)
            Thumbnails.of(bufferedImage)
                    .size(LARGE_DIMENSION, LARGE_DIMENSION)
                    .outputFormat("webp")
                    .outputQuality(0.82f)
                    .toFile(lgDestination.toFile());

            // Geração de Variante Medium (máx 500x500, qualidade 0.80)
            Thumbnails.of(bufferedImage)
                    .size(MEDIUM_DIMENSION, MEDIUM_DIMENSION)
                    .outputFormat("webp")
                    .outputQuality(0.80f)
                    .toFile(mdDestination.toFile());

            // Geração de Variante Small (máx 200x200, qualidade 0.78)
            Thumbnails.of(bufferedImage)
                    .size(SMALL_DIMENSION, SMALL_DIMENSION)
                    .outputFormat("webp")
                    .outputQuality(0.78f)
                    .toFile(smDestination.toFile());

            // Salva também o nome base para consistência canônica
            Thumbnails.of(bufferedImage)
                    .size(LARGE_DIMENSION, LARGE_DIMENSION)
                    .outputFormat("webp")
                    .outputQuality(0.82f)
                    .toFile(baseDestination.toFile());

            return "/uploads/products/" + baseFilename;

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
            String filename = path.contains("/") ? path.substring(path.lastIndexOf('/') + 1) : path;
            if (filename.contains("\\")) {
                filename = filename.substring(filename.lastIndexOf('\\') + 1);
            }
            if (filename.isBlank() || filename.contains("..")) {
                return;
            }

            Path normalizedRoot = root.normalize().toAbsolutePath();
            String rawName = filename.replaceFirst("-(lg|md|sm)\\.webp$", "").replaceFirst("\\.webp$", "");

            // Remove o arquivo base e todas as variantes correspondentes
            String[] variants = new String[] {
                    rawName + ".webp",
                    rawName + "-lg.webp",
                    rawName + "-md.webp",
                    rawName + "-sm.webp"
            };

            for (String variant : variants) {
                Path variantFile = normalizedRoot.resolve(variant).normalize().toAbsolutePath();
                if (variantFile.startsWith(normalizedRoot) && Files.exists(variantFile)) {
                    Files.deleteIfExists(variantFile.toRealPath());
                }
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

            // Fallback gracioso: se a variante específica solicitada não existir, tenta o arquivo base ou variante lg
            if (!Files.exists(file)) {
                String rawName = filename.replaceFirst("-(lg|md|sm)\\.webp$", "").replaceFirst("\\.webp$", "");
                Path fallbackBase = root.resolve(rawName + ".webp").normalize().toAbsolutePath();
                Path fallbackLg = root.resolve(rawName + "-lg.webp").normalize().toAbsolutePath();

                if (fallbackBase.startsWith(normalizedRoot) && Files.exists(fallbackBase)) {
                    file = fallbackBase;
                } else if (fallbackLg.startsWith(normalizedRoot) && Files.exists(fallbackLg)) {
                    file = fallbackLg;
                } else {
                    throw new ImageNotFoundException();
                }
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
