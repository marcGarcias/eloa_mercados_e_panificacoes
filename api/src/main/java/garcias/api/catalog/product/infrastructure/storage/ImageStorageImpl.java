package garcias.api.catalog.product.infrastructure.storage;


import garcias.api.catalog.product.application.storage.ImageStorage;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;


@Component
public class ImageStorageImpl implements ImageStorage {


    private final Path root =
            Paths.get("uploads/products");


    @Override
    public String save(MultipartFile file) {


        try {


            Files.createDirectories(root);



            String extension =
                    getExtension(file);



            String filename =
                    UUID.randomUUID()
                            + extension;



            Path destination =
                    root.resolve(filename);



            Files.copy(
                    file.getInputStream(),
                    destination
            );



            return "/uploads/products/" + filename;



        } catch(IOException exception) {


            throw new RuntimeException(
                    "Could not save image",
                    exception
            );
        }
    }



    private String getExtension(MultipartFile file) {


        String originalName =
                file.getOriginalFilename();



        if(originalName == null) {
            return ".jpg";
        }



        int index =
                originalName.lastIndexOf(".");


        if(index == -1) {
            return ".jpg";
        }



        return originalName
                .substring(index)
                .toLowerCase();
    }




    @Override
    public void delete(String path) {


        if(path == null || path.isBlank()) {
            return;
        }


        try {


            Path file =
                    Paths.get(path.substring(1))
                            .normalize();



            Files.deleteIfExists(file);



        } catch(IOException exception) {


            throw new RuntimeException(
                    "Could not delete image",
                    exception
            );
        }
    }





    @Override
    public Resource load(String filename) {


        try {


            Path file =
                    root.resolve(filename);



            Resource resource =
                    new UrlResource(
                            file.toUri()
                    );



            if(!resource.exists()
                    || !resource.isReadable()) {


                throw new RuntimeException(
                        "Image not found"
                );
            }



            return resource;



        } catch(MalformedURLException exception) {


            throw new RuntimeException(
                    "Could not load image",
                    exception
            );
        }
    }
}