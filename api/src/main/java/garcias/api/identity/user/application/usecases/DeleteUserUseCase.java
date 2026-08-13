package garcias.api.identity.user.application.usecases;

import java.util.UUID;

public interface DeleteUserUseCase {
    void execute(UUID userId);
}
