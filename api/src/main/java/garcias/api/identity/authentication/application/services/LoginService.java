package garcias.api.identity.authentication.application.services;

import garcias.api.identity.authentication.application.dto.events.UserLoggedInEvent;
import garcias.api.identity.authentication.application.dto.requests.LoginRequest;
import garcias.api.identity.authentication.application.dto.results.LoginResult;
import garcias.api.identity.authentication.application.dto.results.UserAuthenticationDto;
import garcias.api.identity.authentication.application.ports.UserAuthenticationPort;
import garcias.api.identity.authentication.application.security.AccessTokenManager;
import garcias.api.identity.authentication.application.security.RefreshTokenManager;
import garcias.api.identity.authentication.application.usecases.LoginUseCase;
import garcias.api.identity.authentication.domain.exceptions.InvalidCredentialsException;
import garcias.api.shared.security.application.PasswordHasher;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class LoginService implements LoginUseCase {

    private final UserAuthenticationPort userAuthenticationPort;
    private final PasswordHasher passwordHasher;
    private final AccessTokenManager accessTokenManager;
    private final RefreshTokenManager refreshTokenManager;
    private final ApplicationEventPublisher eventPublisher;

    public LoginService(
            UserAuthenticationPort userAuthenticationPort,
            PasswordHasher passwordHasher,
            AccessTokenManager accessTokenManager,
            RefreshTokenManager refreshTokenManager,
            ApplicationEventPublisher eventPublisher
    ) {
        this.userAuthenticationPort = userAuthenticationPort;
        this.passwordHasher = passwordHasher;
        this.accessTokenManager = accessTokenManager;
        this.refreshTokenManager = refreshTokenManager;
        this.eventPublisher = eventPublisher;
    }

    public LoginResult execute(LoginRequest request) {

        UserAuthenticationDto user = userAuthenticationPort
                .findByUserCode(request.userCode())
                .orElseThrow(
                        InvalidCredentialsException::new
                );

        if (!"ACTIVE".equals(user.status())) {
            throw new InvalidCredentialsException();
        }

        boolean passwordMatches = passwordHasher.matches(
                request.password(),
                user.hashedPassword()
        );

        if (!passwordMatches) {
            throw new InvalidCredentialsException();
        }

        eventPublisher.publishEvent(new UserLoggedInEvent(user.userCode()));

        String accessToken = accessTokenManager.generate(
                user.userCode(),
                user.role(),
                user.status()
        );

        String refreshToken = refreshTokenManager.generate(
                user.userCode()
        );

        return new LoginResult(
                accessToken,
                refreshToken
        );
    }
}