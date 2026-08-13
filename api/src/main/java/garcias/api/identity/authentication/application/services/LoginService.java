package garcias.api.identity.authentication.application.services;

import garcias.api.identity.authentication.application.dto.requests.LoginRequest;
import garcias.api.identity.authentication.application.dto.responses.LoginResponse;
import garcias.api.identity.authentication.application.usecases.LoginUseCase;
import garcias.api.identity.authentication.domain.exceptions.InvalidCredentialsException;
import garcias.api.identity.authentication.infrastructure.security.jwt.JwtTokenService;
import garcias.api.identity.user.application.security.PasswordHasher;
import garcias.api.identity.user.domain.entities.User;
import garcias.api.identity.user.domain.enums.UserStatus;
import garcias.api.identity.user.domain.repositories.UserRepository;
import garcias.api.identity.user.domain.valueobjects.UserCode;
import org.springframework.stereotype.Service;

@Service
public class LoginService implements LoginUseCase {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final JwtTokenService  jwtTokenService;

    public LoginService(
            UserRepository userRepository,
            PasswordHasher passwordHasher, JwtTokenService jwtTokenService
    ) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
        this.jwtTokenService = jwtTokenService;
    }

    public LoginResponse execute(LoginRequest request) {

        UserCode userCode = new UserCode(
                request.userCode()
        );

        User user = userRepository
                .findByUserCode(userCode)
                .orElseThrow(
                        InvalidCredentialsException::new
                );

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new InvalidCredentialsException();
        }

        boolean passwordMatches = passwordHasher.matches(
                request.password(),
                user.getPassword().value()
        );

        if (!passwordMatches) {
            throw new InvalidCredentialsException();
        }

        user.recordLogin();

        userRepository.save(user);

        String accessToken = jwtTokenService.generateAccessToken(
                user.getUserCode().value(),
                user.getRole().name()
        );

        return new LoginResponse(
                accessToken,
                null
        );
    }
}