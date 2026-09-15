package garcias.api.catalog.product.domain.exceptions;

import garcias.api.shared.exceptions.NotFoundException;

public class ImageNotFoundException extends NotFoundException {
    public ImageNotFoundException() {
        super("Imagem não encontrada.");
    }
}

