package garcias.api.identity.authentication.application.services;

import garcias.api.identity.authentication.application.dto.results.LoginResult;
import garcias.api.identity.authentication.application.security.AccessTokenManager;
import garcias.api.identity.authentication.application.security.RefreshTokenManager;
import garcias.api.identity.authentication.application.usecases.RefreshTokenUseCase;
import garcias.api.identity.authentication.domain.exceptions.InvalidCredentialsException;
import garcias.api.identity.user.domain.entities.User;
import garcias.api.identity.user.domain.enums.UserStatus;
import garcias.api.identity.user.domain.repositories.UserRepository;
import garcias.api.identity.user.domain.valueobjects.UserCode;
import org.springframework.stereotype.Service;

@Service
public class RefreshTokenService implements RefreshTokenUseCase {

    private final RefreshTokenManager refreshTokenManager;
    private final AccessTokenManager accessTokenManager;

    public RefreshTokenService(RefreshTokenManager refreshTokenManager,
                               AccessTokenManager accessTokenManager,
                               UserRepository userRepository)
    {
        this.refreshTokenManager = refreshTokenManager;
        this.accessTokenManager = accessTokenManager;
        this.userRepository = userRepository;
    }

    private final UserRepository userRepository;

    @Override
    public LoginResult execute(String refreshToken) {

        String userCode = refreshTokenManager
                .findUserCode(refreshToken)
                .orElseThrow(
                        InvalidCredentialsException::new
                );

        User user = userRepository
                .findByUserCode(
                        new UserCode(userCode)
                )
                .orElseThrow(
                        InvalidCredentialsException::new
                );

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new InvalidCredentialsException();
        }

        refreshTokenManager.revoke(refreshToken);

        String accessToken = accessTokenManager.generate(
                user.getUserCode().value(),
                user.getRole().name()
        );

        String newRefreshToken = refreshTokenManager.generate(
                user.getUserCode().value()
        );

        return new LoginResult(
                accessToken,
                newRefreshToken
        );
    }
}
