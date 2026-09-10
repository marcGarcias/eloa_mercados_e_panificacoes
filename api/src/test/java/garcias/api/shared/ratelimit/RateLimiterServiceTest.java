package garcias.api.shared.ratelimit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RateLimiterService Unit Tests")
class RateLimiterServiceTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    private RateLimitProperties properties;
    private RateLimiterService rateLimiterService;

    @BeforeEach
    void setUp() {
        properties = new RateLimitProperties();
        properties.setEnabled(true);
        properties.setAuth(new RateLimitProperties.TierConfig(5, 60));
        rateLimiterService = new RateLimiterService(redisTemplate, properties);
    }

    @Test
    @DisplayName("Should allow request when count is within the tier limit")
    void shouldAllowRequestWithinLimit() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.increment("ratelimit:auth:192.168.1.1")).thenReturn(1L);

        RateLimitResult result = rateLimiterService.check("192.168.1.1", RateLimitTier.AUTH);

        assertTrue(result.allowed());
        assertEquals(4L, result.remaining());
        assertEquals(0L, result.retryAfterSeconds());
        assertEquals(5, result.limit());

        verify(redisTemplate).expire("ratelimit:auth:192.168.1.1", Duration.ofSeconds(60));
    }

    @Test
    @DisplayName("Should block request and return retry-after when limit is exceeded")
    void shouldBlockRequestWhenLimitExceeded() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.increment("ratelimit:auth:192.168.1.1")).thenReturn(6L);
        when(redisTemplate.getExpire("ratelimit:auth:192.168.1.1", TimeUnit.SECONDS)).thenReturn(42L);

        RateLimitResult result = rateLimiterService.check("192.168.1.1", RateLimitTier.AUTH);

        assertFalse(result.allowed());
        assertEquals(0L, result.remaining());
        assertEquals(42L, result.retryAfterSeconds());
        assertEquals(5, result.limit());

        verify(redisTemplate, never()).expire(anyString(), any(Duration.class));
    }

    @Test
    @DisplayName("Should fail-open and allow request when Redis throws an exception")
    void shouldFailOpenWhenRedisThrowsException() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.increment(anyString())).thenThrow(new RedisConnectionFailureException("Connection refused"));

        RateLimitResult result = rateLimiterService.check("192.168.1.1", RateLimitTier.AUTH);

        assertTrue(result.allowed());
        assertEquals(5L, result.remaining());
        assertEquals(5, result.limit());
    }

    @Test
    @DisplayName("Should allow all requests when rate limiting is disabled globally")
    void shouldAllowWhenRateLimitIsDisabled() {
        properties.setEnabled(false);

        RateLimitResult result = rateLimiterService.check("192.168.1.1", RateLimitTier.AUTH);

        assertTrue(result.allowed());
        verifyNoInteractions(redisTemplate);
    }
}
