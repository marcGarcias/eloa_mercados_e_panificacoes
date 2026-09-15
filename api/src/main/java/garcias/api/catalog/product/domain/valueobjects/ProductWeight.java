package garcias.api.catalog.product.domain.valueobjects;

import garcias.api.shared.exceptions.AttributeMustBeGreaterThanZeroException;
import garcias.api.shared.exceptions.ValueObjectCannotBeNullException;

import java.math.BigDecimal;

public record ProductWeight(BigDecimal value) {

    public ProductWeight {

        if (value == null) {
            throw new ValueObjectCannotBeNullException("Peso do produto");
        }

        if (value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new AttributeMustBeGreaterThanZeroException("Peso do produto", value);
        }
    }

}
