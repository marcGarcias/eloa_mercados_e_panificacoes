package garcias.api.catalog.product.application.validation;

import garcias.api.shared.exceptions.InvalidImageException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Component
public class WebpImageValidator {


    public void validate(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new InvalidImageException(
                    "Imagem obrigatória"
            );
        }


        if (!isWebp(file)) {

            throw new InvalidImageException(
                    "A imagem deve estar no formato WebP"
            );
        }
    }


    private boolean isWebp(MultipartFile file) {

        try {

            byte[] header = new byte[12];

            file.getInputStream()
                    .read(header);


            String riff =
                    new String(header, 0, 4);


            String webp =
                    new String(header, 8, 4);


            return "RIFF".equals(riff)
                    && "WEBP".equals(webp);


        } catch (IOException exception) {

            return false;
        }
    }
}