package garcias.api.identity.authentication.infrastructure.persistence;

import garcias.api.identity.authentication.domain.repositories.SessionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.*;

@Repository
public class SessionRedisRepository implements SessionRepository {

    private static final Logger log = LoggerFactory.getLogger(SessionRedisRepository.class);

    private static final String SESSION_PREFIX = "session:";
    private static final String REFRESH_PREFIX = "refresh_token:";
    private static final String SESSION_TOKEN_PREFIX = "session_token:";
    private static final String USER_SESSIONS_PREFIX = "user_sessions:";

    private final RedisTemplate<String, String> redisTemplate;
    private final int maxActiveSessionsPerUser;

    @Autowired
    public SessionRedisRepository(
            RedisTemplate<String, String> redisTemplate,
            @Value("${security.session.max-active-sessions-per-user:3}") int maxActiveSessionsPerUser
    ) {
        this.redisTemplate = redisTemplate;
        this.maxActiveSessionsPerUser = maxActiveSessionsPerUser;
    }

    public SessionRedisRepository(RedisTemplate<String, String> redisTemplate) {
        this(redisTemplate, 3);
    }

    @Override
    public void createSession(String sessionId, String userCode, String tokenHash, long ttlSeconds) {
        if (sessionId == null || sessionId.isBlank() || userCode == null || userCode.isBlank()) {
            return;
        }

        Duration ttl = Duration.ofSeconds(ttlSeconds);
        String sessionKey = SESSION_PREFIX + sessionId;
        String userSessionsKey = USER_SESSIONS_PREFIX + userCode;

        redisTemplate.opsForValue().set(sessionKey, userCode, ttl);

        if (tokenHash != null && !tokenHash.isBlank()) {
            redisTemplate.opsForValue().set(SESSION_TOKEN_PREFIX + sessionId, tokenHash, ttl);
        }

        redisTemplate.opsForZSet().add(userSessionsKey, sessionId, System.currentTimeMillis());
        redisTemplate.expire(userSessionsKey, ttl);

        enforceMaxSessions(userCode, userSessionsKey);
    }

    @Override
    public void createSession(String sessionId, String userCode, long ttlSeconds) {
        createSession(sessionId, userCode, null, ttlSeconds);
    }

    private void enforceMaxSessions(String userCode, String userSessionsKey) {
        try {
            Long count = redisTemplate.opsForZSet().zCard(userSessionsKey);
            if (count != null && count > maxActiveSessionsPerUser) {
                long toRemove = count - maxActiveSessionsPerUser;
                Set<String> oldestSessions = redisTemplate.opsForZSet().range(userSessionsKey, 0, toRemove - 1);
                if (oldestSessions != null && !oldestSessions.isEmpty()) {
                    for (String oldSessionId : oldestSessions) {
                        log.info("Revoking excess oldest session [{}] for user [{}] (FIFO policy, limit={})",
                                oldSessionId, userCode, maxActiveSessionsPerUser);
                        revokeSession(oldSessionId, userCode);
                    }
                }
            }
        } catch (Exception exception) {
            log.error("Error enforcing max sessions limit for user [{}]: {}", userCode, exception.getMessage());
        }
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
    public Optional<String> findTokenHashBySessionId(String sessionId) {
        if (sessionId == null || sessionId.isBlank()) {
            return Optional.empty();
        }

        try {
            String tokenHash = redisTemplate.opsForValue().get(SESSION_TOKEN_PREFIX + sessionId);
            return Optional.ofNullable(tokenHash);
        } catch (Exception exception) {
            log.error("Error retrieving tokenHash for session [{}] in Redis: {}", sessionId, exception.getMessage());
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

            String sessionTokenKey = SESSION_TOKEN_PREFIX + sessionId;
            String tokenHash = redisTemplate.opsForValue().get(sessionTokenKey);
            if (tokenHash != null && !tokenHash.isBlank()) {
                redisTemplate.delete(REFRESH_PREFIX + tokenHash);
            }

            redisTemplate.delete(sessionTokenKey);
            redisTemplate.delete(SESSION_PREFIX + sessionId);

            if (resolvedUserCode != null && !resolvedUserCode.isBlank()) {
                redisTemplate.opsForZSet().remove(USER_SESSIONS_PREFIX + resolvedUserCode, sessionId);
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
            Set<String> sessionIds = redisTemplate.opsForZSet().range(userSessionsKey, 0, -1);

            List<String> keysToDelete = new ArrayList<>();
            if (sessionIds != null && !sessionIds.isEmpty()) {
                for (String sessionId : sessionIds) {
                    keysToDelete.add(SESSION_PREFIX + sessionId);
                    String sessionTokenKey = SESSION_TOKEN_PREFIX + sessionId;
                    keysToDelete.add(sessionTokenKey);

                    String tokenHash = redisTemplate.opsForValue().get(sessionTokenKey);
                    if (tokenHash != null && !tokenHash.isBlank()) {
                        keysToDelete.add(REFRESH_PREFIX + tokenHash);
                    }
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
        redisTemplate.opsForValue().set(SESSION_TOKEN_PREFIX + sessionId, tokenHash, ttl);
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
            String refreshKey = REFRESH_PREFIX + tokenHash;
            String sessionId = redisTemplate.opsForValue().get(refreshKey);

            if (sessionId != null && !sessionId.isBlank()) {
                String sessionKey = SESSION_PREFIX + sessionId;
                String sessionTokenKey = SESSION_TOKEN_PREFIX + sessionId;
                String userCode = redisTemplate.opsForValue().get(sessionKey);

                redisTemplate.delete(sessionTokenKey);
                redisTemplate.delete(sessionKey);

                if (userCode != null && !userCode.isBlank()) {
                    redisTemplate.opsForZSet().remove(USER_SESSIONS_PREFIX + userCode, sessionId);
                }
            }

            redisTemplate.delete(refreshKey);
        } catch (Exception exception) {
            log.error("Error revoking refresh token hash in Redis: {}", exception.getMessage());
        }
    }
}
