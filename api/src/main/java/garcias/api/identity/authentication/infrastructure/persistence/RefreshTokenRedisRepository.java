package garcias.api.identity.authentication.infrastructure.persistence;

import garcias.api.identity.authentication.domain.repositories.RefreshTokenRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
                Duration.ofSeconds(expiration)
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

    @Override
    public void deleteByUserCode(
            String userCode
    ) {

        List<String> keysToDelete = new ArrayList<>();

        ScanOptions options = ScanOptions.scanOptions()
                .match(KEY_PREFIX + "*")
                .count(100)
                .build();

        try (var cursor = redisTemplate.scan(options)) {

            cursor.forEachRemaining(key -> {

                String storedUserCode = redisTemplate
                        .opsForValue()
                        .get(key);

                if (userCode.equals(storedUserCode)) {
                    keysToDelete.add(key);
                }
            });
        }

        if (!keysToDelete.isEmpty()) {
            redisTemplate.delete(keysToDelete);
        }
    }

    private String buildKey(String tokenHash) {

        return KEY_PREFIX + tokenHash;
    }
}