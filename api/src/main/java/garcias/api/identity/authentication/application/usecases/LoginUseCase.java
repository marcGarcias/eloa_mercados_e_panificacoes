package garcias.api.identity.authentication.application.usecases;

import garcias.api.identity.authentication.application.dto.requests.LoginRequest;
import garcias.api.identity.authentication.application.dto.responses.LoginResponse;
import garcias.api.identity.authentication.application.dto.results.LoginResult;

public interface LoginUseCase {

    LoginResult execute(LoginRequest request);
}
