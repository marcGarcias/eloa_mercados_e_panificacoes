package garcias.api.identity.authentication.infrastructure.security.refresh;

import garcias.api.identity.authentication.domain.repositories.RefreshTokenRepository;
import garcias.api.identity.authentication.domain.repositories.SessionRepository;
import garcias.api.identity.authentication.infrastructure.security.jwt.JwtProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários do RefreshTokenManagerImpl e RefreshTokenProviderImpl")
class RefreshTokenManagerAndProviderTest {

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    private JwtProperties jwtProperties;
    private RefreshTokenManagerImpl refreshTokenManager;
    private RefreshTokenProviderImpl refreshTokenProvider;

    @BeforeEach
    void setUp() {
        jwtProperties = new JwtProperties();
        jwtProperties.setRefreshTokenExpiration(604800L);

        refreshTokenManager = new RefreshTokenManagerImpl(sessionRepository, jwtProperties);
        refreshTokenProvider = new RefreshTokenProviderImpl(refreshTokenRepository, jwtProperties);
    }

    @Test
    @DisplayName("RefreshTokenManagerImpl deve gerar token e criar sessão no repositório")
    void managerShouldGenerateAndSaveHash() {
        String token = refreshTokenManager.generate("1001", "sess-1001");

        assertThat(token).isNotBlank();
        verify(sessionRepository).createSession(eq("sess-1001"), eq("1001"), eq(604800L));
        verify(sessionRepository).linkRefreshToken(anyString(), eq("sess-1001"), eq(604800L));
    }

    @Test
    @DisplayName("RefreshTokenManagerImpl deve buscar userCode pelo token fornecido")
    void managerShouldFindUserCode() {
        when(sessionRepository.findSessionIdByRefreshTokenHash(anyString())).thenReturn(Optional.of("sess-1001"));
        when(sessionRepository.findUserCodeBySessionId("sess-1001")).thenReturn(Optional.of("1001"));

        Optional<String> userCode = refreshTokenManager.findUserCode("some-refresh-token");

        assertThat(userCode).contains("1001");
        verify(sessionRepository).findSessionIdByRefreshTokenHash(anyString());
        verify(sessionRepository).findUserCodeBySessionId("sess-1001");
    }

    @Test
    @DisplayName("RefreshTokenManagerImpl deve revogar token deletando seu hash e sessão")
    void managerShouldRevokeToken() {
        when(sessionRepository.findSessionIdByRefreshTokenHash(anyString())).thenReturn(Optional.of("sess-1001"));

        refreshTokenManager.revoke("some-refresh-token");

        verify(sessionRepository).revokeRefreshToken(anyString());
        verify(sessionRepository).revokeSession("sess-1001", null);
    }

    @Test
    @DisplayName("RefreshTokenProviderImpl deve gerar token e salvar o hash no repositório")
    void providerShouldGenerateAndSaveHash() {
        String token = refreshTokenProvider.generate("1002");

        assertThat(token).isNotBlank();
        verify(refreshTokenRepository).save(anyString(), eq("1002"), eq(604800L));
    }

    @Test
    @DisplayName("RefreshTokenProviderImpl deve buscar userCode pelo token fornecido")
    void providerShouldFindUserCode() {
        when(refreshTokenRepository.findUserCodeByTokenHash(anyString())).thenReturn(Optional.of("1002"));

        Optional<String> userCode = refreshTokenProvider.findUserCode("other-refresh-token");

        assertThat(userCode).contains("1002");
        verify(refreshTokenRepository).findUserCodeByTokenHash(anyString());
    }
}
