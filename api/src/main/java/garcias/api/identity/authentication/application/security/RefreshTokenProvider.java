package garcias.api.identity.authentication.application.security;

import java.util.Optional;

public interface RefreshTokenProvider {

    String generate(String userCode);

    Optional<String> findUserCode(String refreshToken);

    void revoke(String refreshToken);
}
