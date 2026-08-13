package garcias.api.identity.user.application.usecases;



import garcias.api.identity.user.application.dto.requests.ChangePasswordRequest;

import java.util.UUID;

public interface ChangePasswordUseCase {

    void execute(
            UUID userId,
            ChangePasswordRequest request
    );

}