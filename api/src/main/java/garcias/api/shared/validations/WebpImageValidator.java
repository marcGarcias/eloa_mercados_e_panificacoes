package garcias.api.shared.validations;

import garcias.api.shared.exceptions.InvalidImageException;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;


public class WebpImageValidator {


    public static void validate(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new InvalidImageException("Imagem obrigatória");
        }


        if (!isWebp(file)) {
            throw new InvalidImageException(
                    "A imagem deve estar no formato WebP"
            );
        }

    }


    private static boolean isWebp(MultipartFile file) {

        try {

            byte[] header = new byte[12];

            file.getInputStream().read(header);


            String riff = new String(
                    header,
                    0,
                    4
            );


            String webp = new String(
                    header,
                    8,
                    4
            );


            return "RIFF".equals(riff)
                    && "WEBP".equals(webp);


        } catch (IOException e) {
            return false;
        }
    }

}