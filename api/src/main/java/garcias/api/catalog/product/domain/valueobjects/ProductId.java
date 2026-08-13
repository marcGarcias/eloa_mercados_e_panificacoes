package garcias.api.catalog.product.domain.valueobjects;

import garcias.api.shared.exceptions.AttributeMustBeGreaterThanZeroException;

public record ProductId(Long value) {


    public ProductId {

        if (value != null && value <= 0) {

            throw new AttributeMustBeGreaterThanZeroException(
                    "ProductId",
                    value
            );

        }

    }


    public static ProductId empty() {
        return new ProductId(null);
    }


    public boolean isEmpty() {
        return value == null;
    }

}