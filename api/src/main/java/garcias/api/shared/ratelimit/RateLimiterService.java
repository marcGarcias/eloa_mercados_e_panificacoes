package garcias.api.shared.ratelimit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Service
@org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name = "rate-limit.enabled", havingValue = "true", matchIfMissing = true)
public class RateLimiterService {

    private static final Logger logger = LoggerFactory.getLogger(RateLimiterService.class);
    private static final String KEY_PREFIX = "ratelimit:";

    private final RedisTemplate<String, String> redisTemplate;
    private final RateLimitProperties properties;

    public RateLimiterService(
            RedisTemplate<String, String> redisTemplate,
            RateLimitProperties properties
    ) {
        this.redisTemplate = redisTemplate;
        this.properties = properties;
    }

    public RateLimitResult check(String identifier, RateLimitTier tier) {
        if (!properties.isEnabled()) {
            return RateLimitResult.allowed(999, 999);
        }

        RateLimitProperties.TierConfig config = properties.getConfigFor(tier);
        String key = KEY_PREFIX + tier.name().toLowerCase() + ":" + identifier;

        try {
            Long current = redisTemplate.opsForValue().increment(key);

            if (current != null && current == 1) {
                redisTemplate.expire(key, Duration.ofSeconds(config.getWindowSeconds()));
            }

            if (current != null && current > config.getMaxRequests()) {
                Long ttl = redisTemplate.getExpire(key, TimeUnit.SECONDS);
                long retryAfter = (ttl != null && ttl > 0) ? ttl : config.getWindowSeconds();
                logger.warn("Rate limit excedido para [{}] no nível [{}]. Limite: {}/{}s, Atual: {}, Retry-After: {}s",
                        identifier, tier, config.getMaxRequests(), config.getWindowSeconds(), current, retryAfter);
                return RateLimitResult.blocked(retryAfter, config.getMaxRequests());
            }

            long currentVal = current != null ? current : 0;
            long remaining = Math.max(0, config.getMaxRequests() - currentVal);
            return RateLimitResult.allowed(remaining, config.getMaxRequests());

        } catch (Exception e) {
            logger.warn("Falha ao comunicar com Redis para rate limit. Executando fail-open para [{}]: {}",
                    identifier, e.getMessage());
            return RateLimitResult.allowed(config.getMaxRequests(), config.getMaxRequests());
        }
    }
}

