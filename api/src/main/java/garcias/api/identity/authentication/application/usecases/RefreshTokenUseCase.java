package garcias.api.identity.authentication.application.usecases;

import garcias.api.identity.authentication.application.dto.results.LoginResult;

public interface RefreshTokenUseCase {

    LoginResult execute(String refreshToken);
}
