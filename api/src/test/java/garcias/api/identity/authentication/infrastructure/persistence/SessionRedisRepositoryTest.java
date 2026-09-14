package garcias.api.identity.authentication.infrastructure.persistence;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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
    private SetOperations<String, String> setOperations;

    private SessionRedisRepository sessionRedisRepository;

    @BeforeEach
    void setUp() {
        sessionRedisRepository = new SessionRedisRepository(redisTemplate);
    }

    @Test
    @DisplayName("Should create session and register in user_sessions set")
    void shouldCreateSession() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(redisTemplate.opsForSet()).thenReturn(setOperations);

        sessionRedisRepository.createSession("sess-1", "0001", 3600);

        verify(valueOperations).set("session:sess-1", "0001", Duration.ofSeconds(3600));
        verify(setOperations).add("user_sessions:0001", "sess-1");
        verify(redisTemplate).expire("user_sessions:0001", Duration.ofSeconds(3600));
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
    @DisplayName("Should revoke specific session and remove from user_sessions set")
    void shouldRevokeSpecificSession() {
        when(redisTemplate.opsForSet()).thenReturn(setOperations);

        sessionRedisRepository.revokeSession("sess-1", "0001");

        verify(redisTemplate).delete("session:sess-1");
        verify(setOperations).remove("user_sessions:0001", "sess-1");
    }

    @Test
    @DisplayName("Should revoke all user sessions and delete index set")
    void shouldRevokeAllUserSessions() {
        when(redisTemplate.opsForSet()).thenReturn(setOperations);
        when(setOperations.members("user_sessions:0001")).thenReturn(Set.of("sess-1", "sess-2"));

        sessionRedisRepository.revokeAllUserSessions("0001");

        verify(redisTemplate).delete(argThat((List<String> list) ->
                list.contains("session:sess-1") &&
                list.contains("session:sess-2") &&
                list.contains("user_sessions:0001")
        ));
    }

    @Test
    @DisplayName("Should link and find sessionId by refreshTokenHash")
    void shouldLinkAndFindRefreshToken() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("refresh_token:hash123")).thenReturn("sess-1");

        sessionRedisRepository.linkRefreshToken("hash123", "sess-1", 3600);
        verify(valueOperations).set("refresh_token:hash123", "sess-1", Duration.ofSeconds(3600));

        Optional<String> foundSession = sessionRedisRepository.findSessionIdByRefreshTokenHash("hash123");
        assertTrue(foundSession.isPresent());
        assertEquals("sess-1", foundSession.get());
    }
}
