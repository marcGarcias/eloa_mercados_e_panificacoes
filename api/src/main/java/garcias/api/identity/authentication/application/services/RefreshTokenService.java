package garcias.api.identity.authentication.application.services;

import garcias.api.identity.authentication.application.dto.results.LoginResult;
import garcias.api.identity.authentication.application.dto.results.UserAuthenticationDto;
import garcias.api.identity.authentication.application.ports.UserAuthenticationPort;
import garcias.api.identity.authentication.application.security.AccessTokenManager;
import garcias.api.identity.authentication.application.security.RefreshTokenManager;
import garcias.api.identity.authentication.application.usecases.RefreshTokenUseCase;
import garcias.api.identity.authentication.domain.exceptions.InvalidCredentialsException;
import org.springframework.stereotype.Service;

@Service
public class RefreshTokenService implements RefreshTokenUseCase {

    private final RefreshTokenManager refreshTokenManager;
    private final AccessTokenManager accessTokenManager;
    private final UserAuthenticationPort userAuthenticationPort;

    public RefreshTokenService(RefreshTokenManager refreshTokenManager,
                               AccessTokenManager accessTokenManager,
                               UserAuthenticationPort userAuthenticationPort)
    {
        this.refreshTokenManager = refreshTokenManager;
        this.accessTokenManager = accessTokenManager;
        this.userAuthenticationPort = userAuthenticationPort;
    }

    @Override
    public LoginResult execute(String refreshToken) {

        String userCode = refreshTokenManager
                .findUserCode(refreshToken)
                .orElseThrow(
                        InvalidCredentialsException::new
                );

        UserAuthenticationDto user = userAuthenticationPort
                .findByUserCode(userCode)
                .orElseThrow(
                        InvalidCredentialsException::new
                );

        if (!"ACTIVE".equals(user.status())) {
            throw new InvalidCredentialsException();
        }

        refreshTokenManager.revoke(refreshToken);

        String accessToken = accessTokenManager.generate(
                user.userCode(),
                user.role(),
                user.status()
        );

        String newRefreshToken = refreshTokenManager.generate(
                user.userCode()
        );

        return new LoginResult(
                accessToken,
                newRefreshToken
        );
    }
}
