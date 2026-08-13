package garcias.api.catalog.product.application.services.product.image;

import garcias.api.catalog.product.application.storage.ImageStorage;
import garcias.api.catalog.product.application.usecases.image.LoadProductImageUseCase;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
public class LoadProductImageService implements LoadProductImageUseCase {

    private final ImageStorage imageStorage;

    public LoadProductImageService(
            ImageStorage imageStorage
    ) {
        this.imageStorage = imageStorage;
    }

    @Override
    public Resource execute(String filename) {
        return imageStorage.load(filename);
    }

}