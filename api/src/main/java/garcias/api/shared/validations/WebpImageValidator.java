package garcias.api.shared.validations;

import garcias.api.shared.exceptions.InvalidImageException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

public class WebpImageValidator {

    private static final long MAX_FILE_SIZE_BYTES = 10 * 1024 * 1024; // 10MB

    public static void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidImageException("Imagem obrigatória");
        }

        if (file.getSize() > MAX_FILE_SIZE_BYTES) {
            throw new InvalidImageException("A imagem não pode ultrapassar o limite máximo de 10MB.");
        }

        if (!isValidImageFormat(file)) {
            throw new InvalidImageException("Formato de imagem não suportado. Formatos aceitos: WebP, PNG, JPEG/JPG.");
        }
    }

    private static boolean isValidImageFormat(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            byte[] header = new byte[12];
            int bytesRead = inputStream.read(header);

            if (bytesRead < 8) {
                return false;
            }

            // WebP Check: "RIFF" .... "WEBP"
            if (bytesRead >= 12) {
                String riff = new String(header, 0, 4);
                String webp = new String(header, 8, 4);
                if ("RIFF".equals(riff) && "WEBP".equals(webp)) {
                    return true;
                }
            }

            // JPEG Check: FF D8 FF
            if ((header[0] & 0xFF) == 0xFF && (header[1] & 0xFF) == 0xD8 && (header[2] & 0xFF) == 0xFF) {
                return true;
            }

            // PNG Check: 89 50 4E 47 0D 0A 1A 0A
            if ((header[0] & 0xFF) == 0x89 && (header[1] & 0xFF) == 0x50 &&
                (header[2] & 0xFF) == 0x4E && (header[3] & 0xFF) == 0x47 &&
                (header[4] & 0xFF) == 0x0D && (header[5] & 0xFF) == 0x0A &&
                (header[6] & 0xFF) == 0x1A && (header[7] & 0xFF) == 0x0A) {
                return true;
            }

            return false;
        } catch (IOException e) {
            return false;
        }
    }
}
