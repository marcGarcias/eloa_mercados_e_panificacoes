package garcias.api.catalog.product.infrastructure.presentation.shared;

import garcias.api.catalog.product.application.usecases.image.LoadProductImageUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/storage/images")
public class ProductImageController {

    private final LoadProductImageUseCase loadProductImageUseCase;

    public ProductImageController(
            LoadProductImageUseCase loadProductImageUseCase
    ) {
        this.loadProductImageUseCase = loadProductImageUseCase;
    }

    @Operation(
            summary = "Obter imagem de um produto",
            description = """
                    Retorna o arquivo da imagem de um produto.

                    A imagem é localizada a partir do nome do arquivo informado na URL.
                    O Content-Type é definido automaticamente conforme a extensão do arquivo.
                    """
    )
    @ApiResponse(
            responseCode = "200",
            description = "Imagem encontrada e retornada com sucesso",
            content = @Content(
                    mediaType = "image/*"
            )
    )
    @ApiResponse(
            responseCode = "404",
            description = "Imagem não encontrada",
            content = @Content
    )
    @GetMapping("/{filename}")
    public ResponseEntity<Resource> getImage(

            @Parameter(
                    description = "Nome do arquivo da imagem.",
                    example = "produto-15.webp",
                    required = true
            )
            @PathVariable
            String filename

    ) {

        Resource resource =
                loadProductImageUseCase.execute(filename);

        MediaType mediaType =
                MediaTypeFactory
                        .getMediaType(resource)
                        .orElse(MediaType.APPLICATION_OCTET_STREAM);

        return ResponseEntity
                .ok()
                .contentType(mediaType)
                .cacheControl(
                        CacheControl.maxAge(30, TimeUnit.DAYS)
                )
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + resource.getFilename() + "\""
                )
                .body(resource);
    }
}
