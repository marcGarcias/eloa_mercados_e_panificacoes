package garcias.api.catalog.product.application.storage;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface ImageStorage {

    String save(MultipartFile file);

    void delete(String path);

    Resource load(String filename);
}