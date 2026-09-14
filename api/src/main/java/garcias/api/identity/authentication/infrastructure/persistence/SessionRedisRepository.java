package garcias.api.identity.authentication.infrastructure.persistence;

import garcias.api.identity.authentication.domain.repositories.SessionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.*;

@Repository
public class SessionRedisRepository implements SessionRepository {

    private static final Logger log = LoggerFactory.getLogger(SessionRedisRepository.class);

    private static final String SESSION_PREFIX = "session:";
    private static final String REFRESH_PREFIX = "refresh_token:";
    private static final String USER_SESSIONS_PREFIX = "user_sessions:";

    private final RedisTemplate<String, String> redisTemplate;

    public SessionRedisRepository(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void createSession(String sessionId, String userCode, long ttlSeconds) {
        if (sessionId == null || sessionId.isBlank() || userCode == null || userCode.isBlank()) {
            return;
        }

        Duration ttl = Duration.ofSeconds(ttlSeconds);
        String sessionKey = SESSION_PREFIX + sessionId;
        String userSessionsKey = USER_SESSIONS_PREFIX + userCode;

        redisTemplate.opsForValue().set(sessionKey, userCode, ttl);
        redisTemplate.opsForSet().add(userSessionsKey, sessionId);
        redisTemplate.expire(userSessionsKey, ttl);
    }

    @Override
    public boolean isSessionActive(String sessionId) {
        if (sessionId == null || sessionId.isBlank()) {
            return false;
        }

        try {
            Boolean hasKey = redisTemplate.hasKey(SESSION_PREFIX + sessionId);
            return Boolean.TRUE.equals(hasKey);
        } catch (Exception exception) {
            log.error("Fail-closed: Error checking active session [{}] in Redis: {}", sessionId, exception.getMessage());
            return false;
        }
    }

    @Override
    public Optional<String> findUserCodeBySessionId(String sessionId) {
        if (sessionId == null || sessionId.isBlank()) {
            return Optional.empty();
        }

        try {
            String userCode = redisTemplate.opsForValue().get(SESSION_PREFIX + sessionId);
            return Optional.ofNullable(userCode);
        } catch (Exception exception) {
            log.error("Error retrieving userCode for session [{}] in Redis: {}", sessionId, exception.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public void revokeSession(String sessionId, String userCode) {
        if (sessionId == null || sessionId.isBlank()) {
            return;
        }

        try {
            String resolvedUserCode = userCode;
            if (resolvedUserCode == null || resolvedUserCode.isBlank()) {
                resolvedUserCode = redisTemplate.opsForValue().get(SESSION_PREFIX + sessionId);
            }

            redisTemplate.delete(SESSION_PREFIX + sessionId);

            if (resolvedUserCode != null && !resolvedUserCode.isBlank()) {
                redisTemplate.opsForSet().remove(USER_SESSIONS_PREFIX + resolvedUserCode, sessionId);
            }
        } catch (Exception exception) {
            log.error("Error revoking session [{}] in Redis: {}", sessionId, exception.getMessage());
        }
    }

    @Override
    public void revokeAllUserSessions(String userCode) {
        if (userCode == null || userCode.isBlank()) {
            return;
        }

        try {
            String userSessionsKey = USER_SESSIONS_PREFIX + userCode;
            Set<String> sessionIds = redisTemplate.opsForSet().members(userSessionsKey);

            List<String> keysToDelete = new ArrayList<>();
            if (sessionIds != null && !sessionIds.isEmpty()) {
                for (String sessionId : sessionIds) {
                    keysToDelete.add(SESSION_PREFIX + sessionId);
                }
            }
            keysToDelete.add(userSessionsKey);

            redisTemplate.delete(keysToDelete);
        } catch (Exception exception) {
            log.error("Error revoking all sessions for user [{}] in Redis: {}", userCode, exception.getMessage());
        }
    }

    @Override
    public void linkRefreshToken(String tokenHash, String sessionId, long ttlSeconds) {
        if (tokenHash == null || tokenHash.isBlank() || sessionId == null || sessionId.isBlank()) {
            return;
        }

        Duration ttl = Duration.ofSeconds(ttlSeconds);
        redisTemplate.opsForValue().set(REFRESH_PREFIX + tokenHash, sessionId, ttl);
    }

    @Override
    public Optional<String> findSessionIdByRefreshTokenHash(String tokenHash) {
        if (tokenHash == null || tokenHash.isBlank()) {
            return Optional.empty();
        }

        try {
            String sessionId = redisTemplate.opsForValue().get(REFRESH_PREFIX + tokenHash);
            return Optional.ofNullable(sessionId);
        } catch (Exception exception) {
            log.error("Error retrieving sessionId for token hash in Redis: {}", exception.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public void revokeRefreshToken(String tokenHash) {
        if (tokenHash == null || tokenHash.isBlank()) {
            return;
        }

        try {
            redisTemplate.delete(REFRESH_PREFIX + tokenHash);
        } catch (Exception exception) {
            log.error("Error revoking refresh token hash in Redis: {}", exception.getMessage());
        }
    }
}
