package garcias.api.identity.authentication.application.usecases;

import garcias.api.identity.authentication.application.dto.requests.LoginRequest;
import garcias.api.identity.authentication.application.dto.responses.LoginResponse;

public interface LoginUseCase {

    LoginResponse execute(LoginRequest request);
}
