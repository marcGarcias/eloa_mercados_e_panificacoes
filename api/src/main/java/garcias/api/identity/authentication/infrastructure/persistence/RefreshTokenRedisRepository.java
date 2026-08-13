package garcias.api.identity.authentication.infrastructure.persistence;

import garcias.api.identity.authentication.domain.repositories.RefreshTokenRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Repository
public class RefreshTokenRedisRepository implements RefreshTokenRepository {

    private static final String KEY_PREFIX = "refresh_token:";

    private final RedisTemplate<String, String> redisTemplate;

    public RefreshTokenRedisRepository(
            RedisTemplate<String, String> redisTemplate
    ) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void save(
            String tokenHash,
            String userCode,
            long expiration
    ) {

        redisTemplate.opsForValue().set(
                buildKey(tokenHash),
                userCode,
                expiration,
                TimeUnit.MILLISECONDS
        );
    }

    @Override
    public Optional<String> findUserCodeByTokenHash(
            String tokenHash
    ) {

        String userCode = redisTemplate
                .opsForValue()
                .get(buildKey(tokenHash));

        return Optional.ofNullable(userCode);
    }

    @Override
    public void deleteByTokenHash(
            String tokenHash
    ) {

        redisTemplate.delete(
                buildKey(tokenHash)
        );
    }

    private String buildKey(String tokenHash) {

        return KEY_PREFIX + tokenHash;
    }
}