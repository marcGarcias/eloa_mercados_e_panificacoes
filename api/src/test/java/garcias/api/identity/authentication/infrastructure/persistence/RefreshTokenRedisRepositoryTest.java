package garcias.api.identity.authentication.infrastructure.persistence;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RefreshTokenRedisRepository Security Unit Tests")
class RefreshTokenRedisRepositoryTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Mock
    private Cursor<String> cursor;

    private RefreshTokenRedisRepository repository;

    @BeforeEach
    void setUp() {
        repository = new RefreshTokenRedisRepository(redisTemplate);
    }

    @Test
    @DisplayName("Should save refresh token hash with TTL in Redis")
    void shouldSaveTokenWithTtl() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        repository.save("hash123", "0001", 604800L);

        verify(valueOperations).set("refresh_token:hash123", "0001", Duration.ofSeconds(604800L));
    }

    @Test
    @DisplayName("Should find userCode by token hash when present")
    void shouldFindUserCodeByTokenHash() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("refresh_token:hash123")).thenReturn("0001");

        Optional<String> result = repository.findUserCodeByTokenHash("hash123");

        assertTrue(result.isPresent());
        assertEquals("0001", result.get());
    }

    @Test
    @DisplayName("Should return empty Optional when token hash is not in Redis or expired")
    void shouldReturnEmptyWhenTokenNotFound() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("refresh_token:nonexistent")).thenReturn(null);

        Optional<String> result = repository.findUserCodeByTokenHash("nonexistent");

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should delete token by token hash on single session logout")
    void shouldDeleteByTokenHash() {
        repository.deleteByTokenHash("hash123");

        verify(redisTemplate).delete("refresh_token:hash123");
    }

    @Test
    @DisplayName("Should delete all user tokens on global session revocation")
    void shouldDeleteAllUserTokens() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(redisTemplate.scan(any(ScanOptions.class))).thenReturn(cursor);

        doAnswer(invocation -> {
            java.util.function.Consumer<String> consumer = invocation.getArgument(0);
            consumer.accept("refresh_token:tokenA");
            consumer.accept("refresh_token:tokenB");
            return null;
        }).when(cursor).forEachRemaining(any());

        when(valueOperations.get("refresh_token:tokenA")).thenReturn("0001");
        when(valueOperations.get("refresh_token:tokenB")).thenReturn("0002");

        repository.deleteByUserCode("0001");

        verify(redisTemplate).delete(List.of("refresh_token:tokenA"));
    }
}

