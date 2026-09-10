package garcias.api.identity.authentication.application.services;

import garcias.api.identity.authentication.application.dto.events.UserLoggedInEvent;
import garcias.api.identity.authentication.application.dto.requests.LoginRequest;
import garcias.api.identity.authentication.application.dto.results.LoginResult;
import garcias.api.identity.authentication.application.dto.results.UserAuthenticationDto;
import garcias.api.identity.authentication.application.ports.UserAuthenticationPort;
import garcias.api.identity.authentication.application.security.AccessTokenManager;
import garcias.api.identity.authentication.application.security.RefreshTokenManager;
import garcias.api.identity.authentication.domain.exceptions.InvalidCredentialsException;
import garcias.api.shared.security.application.PasswordHasher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("LoginService Security Unit Tests")
class LoginServiceTest {

    @Mock
    private UserAuthenticationPort userAuthenticationPort;

    @Mock
    private PasswordHasher passwordHasher;

    @Mock
    private AccessTokenManager accessTokenManager;

    @Mock
    private RefreshTokenManager refreshTokenManager;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    private LoginService loginService;

    @BeforeEach
    void setUp() {
        loginService = new LoginService(
                userAuthenticationPort,
                passwordHasher,
                accessTokenManager,
                refreshTokenManager,
                eventPublisher
        );
    }

    @Test
    @DisplayName("Should login successfully with valid credentials and active user")
    void shouldLoginSuccessfully() {
        LoginRequest request = new LoginRequest("0001", "correctPassword");
        UserAuthenticationDto user = new UserAuthenticationDto(
                "0001",
                "hashedPass123",
                "SUPER_ADMIN",
                "ACTIVE"
        );

        when(userAuthenticationPort.findByUserCode("0001")).thenReturn(Optional.of(user));
        when(passwordHasher.matches("correctPassword", "hashedPass123")).thenReturn(true);
        when(accessTokenManager.generate("0001", "SUPER_ADMIN", "ACTIVE")).thenReturn("access.jwt.token");
        when(refreshTokenManager.generate("0001")).thenReturn("refresh.random.token");

        LoginResult result = loginService.execute(request);

        assertNotNull(result);
        assertEquals("access.jwt.token", result.accessToken());
        assertEquals("refresh.random.token", result.refreshToken());

        ArgumentCaptor<UserLoggedInEvent> eventCaptor = ArgumentCaptor.forClass(UserLoggedInEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        assertEquals("0001", eventCaptor.getValue().userCode());
    }

    @Test
    @DisplayName("Should throw InvalidCredentialsException when user is not found")
    void shouldThrowExceptionWhenUserNotFound() {
        LoginRequest request = new LoginRequest("9999", "password");
        when(userAuthenticationPort.findByUserCode("9999")).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class, () -> loginService.execute(request));
        verifyNoInteractions(passwordHasher, accessTokenManager, refreshTokenManager, eventPublisher);
    }

    @Test
    @DisplayName("Should throw InvalidCredentialsException when user is INACTIVE")
    void shouldThrowExceptionWhenUserIsInactive() {
        LoginRequest request = new LoginRequest("0001", "password");
        UserAuthenticationDto user = new UserAuthenticationDto(
                "0001",
                "hashedPass",
                "SUPER_ADMIN",
                "INACTIVE"
        );

        when(userAuthenticationPort.findByUserCode("0001")).thenReturn(Optional.of(user));

        assertThrows(InvalidCredentialsException.class, () -> loginService.execute(request));
        verifyNoInteractions(passwordHasher, accessTokenManager, refreshTokenManager, eventPublisher);
    }

    @Test
    @DisplayName("Should throw InvalidCredentialsException when password does not match")
    void shouldThrowExceptionWhenPasswordDoesNotMatch() {
        LoginRequest request = new LoginRequest("0001", "wrongPassword");
        UserAuthenticationDto user = new UserAuthenticationDto(
                "0001",
                "hashedPass",
                "SUPER_ADMIN",
                "ACTIVE"
        );

        when(userAuthenticationPort.findByUserCode("0001")).thenReturn(Optional.of(user));
        when(passwordHasher.matches("wrongPassword", "hashedPass")).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> loginService.execute(request));
        verify(eventPublisher, never()).publishEvent(any());
        verifyNoInteractions(accessTokenManager, refreshTokenManager);
    }
}
