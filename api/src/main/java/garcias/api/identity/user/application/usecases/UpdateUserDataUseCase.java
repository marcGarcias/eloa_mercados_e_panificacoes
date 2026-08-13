package garcias.api.identity.user.application.usecases;

import garcias.api.identity.user.application.dto.requests.UpdateUserDataRequest;

import java.util.UUID;

public interface UpdateUserDataUseCase {

    void execute(
            UUID userId,
            UpdateUserDataRequest request
    );
}
