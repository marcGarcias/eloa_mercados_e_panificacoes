package garcias.api.identity.authentication.application.services;

import garcias.api.identity.authentication.application.dto.results.LoginResult;
import garcias.api.identity.authentication.application.dto.results.UserAuthenticationDto;
import garcias.api.identity.authentication.application.ports.UserAuthenticationPort;
import garcias.api.identity.authentication.application.security.AccessTokenManager;
import garcias.api.identity.authentication.application.security.RefreshTokenManager;
import garcias.api.identity.authentication.domain.exceptions.InvalidCredentialsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RefreshTokenService Security Unit Tests")
class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenManager refreshTokenManager;

    @Mock
    private AccessTokenManager accessTokenManager;

    @Mock
    private UserAuthenticationPort userAuthenticationPort;

    private RefreshTokenService refreshTokenService;

    @BeforeEach
    void setUp() {
        refreshTokenService = new RefreshTokenService(
                refreshTokenManager,
                accessTokenManager,
                userAuthenticationPort
        );
    }

    @Test
    @DisplayName("Should rotate tokens successfully when refresh token is valid and user is active")
    void shouldRotateTokensSuccessfully() {
        String oldRefreshToken = "old.refresh.token";
        UserAuthenticationDto user = new UserAuthenticationDto(
                "0001",
                "hashedPass",
                "ADMIN",
                "ACTIVE"
        );

        when(refreshTokenManager.findUserCode(oldRefreshToken)).thenReturn(Optional.of("0001"));
        when(userAuthenticationPort.findByUserCode("0001")).thenReturn(Optional.of(user));
        when(accessTokenManager.generate("0001", "ADMIN", "ACTIVE")).thenReturn("new.access.token");
        when(refreshTokenManager.generate("0001")).thenReturn("new.refresh.token");

        LoginResult result = refreshTokenService.execute(oldRefreshToken);

        assertNotNull(result);
        assertEquals("new.access.token", result.accessToken());
        assertEquals("new.refresh.token", result.refreshToken());

        verify(refreshTokenManager).revoke(oldRefreshToken);
    }

    @Test
    @DisplayName("Should throw InvalidCredentialsException when refresh token does not exist or expired in Redis")
    void shouldThrowExceptionWhenRefreshTokenNotFound() {
        when(refreshTokenManager.findUserCode("invalid.token")).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class, () -> refreshTokenService.execute("invalid.token"));
        verify(refreshTokenManager, never()).revoke(anyString());
        verifyNoInteractions(accessTokenManager, userAuthenticationPort);
    }

    @Test
    @DisplayName("Should throw InvalidCredentialsException when user is inactive at refresh time")
    void shouldThrowExceptionWhenUserIsInactive() {
        UserAuthenticationDto user = new UserAuthenticationDto(
                "0001",
                "hashedPass",
                "ADMIN",
                "BLOCKED"
        );

        when(refreshTokenManager.findUserCode("valid.token")).thenReturn(Optional.of("0001"));
        when(userAuthenticationPort.findByUserCode("0001")).thenReturn(Optional.of(user));

        assertThrows(InvalidCredentialsException.class, () -> refreshTokenService.execute("valid.token"));
        verify(refreshTokenManager, never()).revoke(anyString());
        verifyNoInteractions(accessTokenManager);
    }
}

