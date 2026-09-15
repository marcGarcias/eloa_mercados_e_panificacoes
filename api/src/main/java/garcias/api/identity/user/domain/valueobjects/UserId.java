package garcias.api.identity.user.domain.valueobjects;

import java.util.Objects;
import java.util.UUID;

public record UserId(UUID value) {

    public UserId {
        Objects.requireNonNull(value, "O identificador do usuário não pode ser nulo");
    }

    public static UserId generate() {
        return new UserId(UUID.randomUUID());
    }
}

