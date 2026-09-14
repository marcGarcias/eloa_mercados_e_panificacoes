package garcias.api.identity.authentication.application.services;

import garcias.api.identity.authentication.application.security.RefreshTokenManager;
import garcias.api.identity.authentication.domain.repositories.SessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("LogoutService Security Unit Tests")
class LogoutServiceTest {

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private RefreshTokenManager refreshTokenManager;

    private LogoutService logoutService;

    @BeforeEach
    void setUp() {
        logoutService = new LogoutService(sessionRepository, refreshTokenManager);
    }

    @Test
    @DisplayName("Should delete all sessions associated with userCode on global logout")
    void shouldDeleteTokensByUserCode() {
        logoutService.execute("0001");

        verify(sessionRepository).revokeAllUserSessions("0001");
    }

    @Test
    @DisplayName("Should revoke specific refresh token on token logout")
    void shouldRevokeSpecificRefreshToken() {
        logoutService.executeByToken("sample.refresh.token");

        verify(refreshTokenManager).revoke("sample.refresh.token");
    }

    @Test
    @DisplayName("Should revoke specific session by sessionId")
    void shouldRevokeSpecificSessionBySessionId() {
        logoutService.executeBySessionId("sess-123", "0001");

        verify(sessionRepository).revokeSession("sess-123", "0001");
    }

    @Test
    @DisplayName("Should safely ignore null or blank userCode")
    void shouldSafelyIgnoreBlankUserCode() {
        logoutService.execute(null);
        logoutService.execute("   ");

        verifyNoInteractions(sessionRepository);
    }

    @Test
    @DisplayName("Should safely ignore null or blank refreshToken")
    void shouldSafelyIgnoreBlankRefreshToken() {
        logoutService.executeByToken(null);
        logoutService.executeByToken("   ");

        verifyNoInteractions(refreshTokenManager);
    }
}

