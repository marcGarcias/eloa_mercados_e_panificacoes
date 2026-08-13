package garcias.api.identity.authentication.application.usecases;

import garcias.api.identity.authentication.application.dto.requests.BootstrapUserRequest;
import garcias.api.identity.authentication.application.dto.responses.BootstrapUserResponse;

public interface BootstrapUserUseCase {

    BootstrapUserResponse execute(
            BootstrapUserRequest request
    );
}