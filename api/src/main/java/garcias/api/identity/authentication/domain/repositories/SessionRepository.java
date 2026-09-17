package garcias.api.identity.authentication.domain.repositories;

import java.util.Optional;

public interface SessionRepository {

    void createSession(String sessionId, String userCode, String tokenHash, long ttlSeconds);

    default void createSession(String sessionId, String userCode, long ttlSeconds) {
        createSession(sessionId, userCode, null, ttlSeconds);
    }

    boolean isSessionActive(String sessionId);

    Optional<String> findUserCodeBySessionId(String sessionId);

    Optional<String> findTokenHashBySessionId(String sessionId);

    void revokeSession(String sessionId, String userCode);

    void revokeAllUserSessions(String userCode);

    void linkRefreshToken(String tokenHash, String sessionId, long ttlSeconds);

    Optional<String> findSessionIdByRefreshTokenHash(String tokenHash);

    void revokeRefreshToken(String tokenHash);
}
