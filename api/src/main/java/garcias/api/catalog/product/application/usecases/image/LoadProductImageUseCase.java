package garcias.api.catalog.product.application.usecases.image;

import org.springframework.core.io.Resource;

public interface LoadProductImageUseCase {
    Resource execute(String filename);
}
