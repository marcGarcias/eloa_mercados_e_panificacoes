package garcias.api.identity.authentication.infrastructure.persistence;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SessionRedisRepository Unit Tests")
class SessionRedisRepositoryTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Mock
    private ZSetOperations<String, String> zSetOperations;

    private SessionRedisRepository sessionRedisRepository;

    @BeforeEach
    void setUp() {
        sessionRedisRepository = new SessionRedisRepository(redisTemplate, 3);
    }

    @Test
    @DisplayName("Should create session and register in user_sessions zset and session_token")
    void shouldCreateSession() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
        when(zSetOperations.zCard("user_sessions:0001")).thenReturn(1L);

        sessionRedisRepository.createSession("sess-1", "0001", "hash1", 3600);

        verify(valueOperations).set("session:sess-1", "0001", Duration.ofSeconds(3600));
        verify(valueOperations).set("session_token:sess-1", "hash1", Duration.ofSeconds(3600));
        verify(zSetOperations).add(eq("user_sessions:0001"), eq("sess-1"), anyDouble());
        verify(redisTemplate).expire("user_sessions:0001", Duration.ofSeconds(3600));
    }

    @Test
    @DisplayName("Should enforce FIFO policy and revoke oldest session when exceeding limit of 3")
    void shouldEnforceFifoLimit() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
        // Excedeu o limite de 3 (tem 4 agora)
        when(zSetOperations.zCard("user_sessions:0001")).thenReturn(4L);
        when(zSetOperations.range("user_sessions:0001", 0, 0)).thenReturn(Set.of("sess-oldest"));
        when(valueOperations.get("session_token:sess-oldest")).thenReturn("hash-oldest");

        sessionRedisRepository.createSession("sess-new", "0001", "hash-new", 3600);

        // Verifica que a mais antiga foi revogada em cascata
        verify(redisTemplate).delete("refresh_token:hash-oldest");
        verify(redisTemplate).delete("session_token:sess-oldest");
        verify(redisTemplate).delete("session:sess-oldest");
        verify(zSetOperations).remove("user_sessions:0001", "sess-oldest");
    }

    @Test
    @DisplayName("Should return true when session key exists in Redis")
    void shouldReturnTrueWhenSessionExists() {
        when(redisTemplate.hasKey("session:sess-1")).thenReturn(true);

        boolean active = sessionRedisRepository.isSessionActive("sess-1");

        assertTrue(active);
    }

    @Test
    @DisplayName("Should return false when session key does not exist or Redis fails (fail-closed)")
    void shouldReturnFalseWhenSessionDoesNotExistOrFails() {
        when(redisTemplate.hasKey("session:sess-expired")).thenReturn(false);

        boolean active = sessionRedisRepository.isSessionActive("sess-expired");
        assertFalse(active);

        when(redisTemplate.hasKey("session:sess-error")).thenThrow(new RuntimeException("Redis connection error"));
        boolean failClosedActive = sessionRedisRepository.isSessionActive("sess-error");
        assertFalse(failClosedActive);
    }

    @Test
    @DisplayName("Should revoke specific session and clean associated refresh token and reverse link")
    void shouldRevokeSpecificSession() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
        when(valueOperations.get("session_token:sess-1")).thenReturn("hash-123");

        sessionRedisRepository.revokeSession("sess-1", "0001");

        verify(redisTemplate).delete("refresh_token:hash-123");
        verify(redisTemplate).delete("session_token:sess-1");
        verify(redisTemplate).delete("session:sess-1");
        verify(zSetOperations).remove("user_sessions:0001", "sess-1");
    }

    @Test
    @DisplayName("Should revoke all user sessions and delete index set and token links")
    void shouldRevokeAllUserSessions() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
        when(zSetOperations.range("user_sessions:0001", 0, -1)).thenReturn(Set.of("sess-1", "sess-2"));
        when(valueOperations.get("session_token:sess-1")).thenReturn("hash1");
        when(valueOperations.get("session_token:sess-2")).thenReturn(null);

        sessionRedisRepository.revokeAllUserSessions("0001");

        verify(redisTemplate).delete(argThat((List<String> list) ->
                list.contains("session:sess-1") &&
                list.contains("session:sess-2") &&
                list.contains("session_token:sess-1") &&
                list.contains("session_token:sess-2") &&
                list.contains("refresh_token:hash1") &&
                list.contains("user_sessions:0001")
        ));
    }

    @Test
    @DisplayName("Should link and find sessionId by refreshTokenHash with reverse mapping")
    void shouldLinkAndFindRefreshToken() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("refresh_token:hash123")).thenReturn("sess-1");

        sessionRedisRepository.linkRefreshToken("hash123", "sess-1", 3600);
        verify(valueOperations).set("refresh_token:hash123", "sess-1", Duration.ofSeconds(3600));
        verify(valueOperations).set("session_token:sess-1", "hash123", Duration.ofSeconds(3600));

        Optional<String> foundSession = sessionRedisRepository.findSessionIdByRefreshTokenHash("hash123");
        assertTrue(foundSession.isPresent());
        assertEquals("sess-1", foundSession.get());
    }

    @Test
    @DisplayName("Should revoke refresh token and cascade delete session and reverse link")
    void shouldRevokeRefreshTokenCascade() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
        when(valueOperations.get("refresh_token:hash123")).thenReturn("sess-1");
        when(valueOperations.get("session_token:sess-1")).thenReturn("hash123");
        when(valueOperations.get("session:sess-1")).thenReturn("0001");

        sessionRedisRepository.revokeRefreshToken("hash123");

        verify(redisTemplate).delete("session_token:sess-1");
        verify(redisTemplate).delete("session:sess-1");
        verify(zSetOperations).remove("user_sessions:0001", "sess-1");
        verify(redisTemplate).delete("refresh_token:hash123");
    }

    @Test
    @DisplayName("Should preserve session and only delete old token key when revoking rotated token")
    void shouldPreserveSessionWhenRevokingOldRotatedRefreshToken() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("refresh_token:old-hash")).thenReturn("sess-1");
        // Sessão já aponta para o novo hash rotacionado
        when(valueOperations.get("session_token:sess-1")).thenReturn("new-hash");

        sessionRedisRepository.revokeRefreshToken("old-hash");

        // Apenas o hash antigo deve ser deletado, a sessão deve ser preservada!
        verify(redisTemplate).delete("refresh_token:old-hash");
        verify(redisTemplate, never()).delete("session:sess-1");
        verify(redisTemplate, never()).delete("session_token:sess-1");
    }
}
