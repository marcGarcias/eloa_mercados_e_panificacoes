package garcias.api.identity.authentication.application.security;

import java.util.Optional;

public interface RefreshTokenManager {

    String generate(String userCode, String sessionId);

    String generate(String userCode);

    Optional<String> findSessionId(String refreshToken);

    Optional<String> findUserCode(String refreshToken);

    void revoke(String refreshToken);
}

