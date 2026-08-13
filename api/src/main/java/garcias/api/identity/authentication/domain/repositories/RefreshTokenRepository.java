package garcias.api.identity.authentication.domain.repositories;

import java.util.Optional;

public interface RefreshTokenRepository {

    void save(
            String tokenHash,
            String userCode,
            long expiration
    );

    Optional<String> findUserCodeByTokenHash(
            String tokenHash
    );

    void deleteByTokenHash(
            String tokenHash
    );
}