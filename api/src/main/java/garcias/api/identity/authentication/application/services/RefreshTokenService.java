package garcias.api.identity.authentication.application.services;

import garcias.api.identity.authentication.application.dto.results.LoginResult;
import garcias.api.identity.authentication.application.security.AccessTokenProvider;
import garcias.api.identity.authentication.application.security.RefreshTokenProvider;
import garcias.api.identity.authentication.application.usecases.RefreshTokenUseCase;
import garcias.api.identity.authentication.domain.exceptions.InvalidCredentialsException;
import garcias.api.identity.user.domain.entities.User;
import garcias.api.identity.user.domain.enums.UserStatus;
import garcias.api.identity.user.domain.repositories.UserRepository;
import garcias.api.identity.user.domain.valueobjects.UserCode;
import org.springframework.stereotype.Service;

@Service
public class RefreshTokenService implements RefreshTokenUseCase {

    private final RefreshTokenProvider refreshTokenProvider;
    private final AccessTokenProvider accessTokenProvider;

    public RefreshTokenService(RefreshTokenProvider refreshTokenProvider,
                               AccessTokenProvider accessTokenProvider,
                               UserRepository userRepository)
    {
        this.refreshTokenProvider = refreshTokenProvider;
        this.accessTokenProvider = accessTokenProvider;
        this.userRepository = userRepository;
    }

    private final UserRepository userRepository;

    @Override
    public LoginResult execute(String refreshToken) {

        String userCode = refreshTokenProvider
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

        refreshTokenProvider.revoke(refreshToken);

        String accessToken = accessTokenProvider.generate(
                user.getUserCode().value(),
                user.getRole().name()
        );

        String newRefreshToken = refreshTokenProvider.generate(
                user.getUserCode().value()
        );

        return new LoginResult(
                accessToken,
                newRefreshToken
        );
    }
}
