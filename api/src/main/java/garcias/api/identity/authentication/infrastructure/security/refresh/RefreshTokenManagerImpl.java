package garcias.api.identity.authentication.infrastructure.security.refresh;

import garcias.api.identity.authentication.application.security.RefreshTokenManager;
import garcias.api.identity.authentication.domain.repositories.SessionRepository;
import garcias.api.identity.authentication.infrastructure.security.exceptions.TokenGenerationException;
import garcias.api.identity.authentication.infrastructure.security.jwt.JwtProperties;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Optional;

@Service
public class RefreshTokenManagerImpl implements RefreshTokenManager {

    private final SessionRepository sessionRepository;
    private final JwtProperties jwtProperties;
    private final SecureRandom secureRandom;

    public RefreshTokenManagerImpl(
            SessionRepository sessionRepository,
            JwtProperties jwtProperties
    ) {
        this.sessionRepository = sessionRepository;
        this.jwtProperties = jwtProperties;
        this.secureRandom = new SecureRandom();
    }

    @Override
    public String generate(String userCode, String sessionId) {

        String targetSessionId = (sessionId != null && !sessionId.isBlank())
                ? sessionId
                : java.util.UUID.randomUUID().toString();

        byte[] tokenBytes = new byte[32];
        secureRandom.nextBytes(tokenBytes);

        String token = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(tokenBytes);

        String tokenHash = hash(token);

        long expiration = jwtProperties.getRefreshTokenExpiration();
        sessionRepository.createSession(targetSessionId, userCode, tokenHash, expiration);
        sessionRepository.linkRefreshToken(tokenHash, targetSessionId, expiration);

        return token;
    }

    @Override
    public String generate(String userCode) {
        return generate(userCode, java.util.UUID.randomUUID().toString());
    }

    @Override
    public Optional<String> findSessionId(String refreshToken) {
        String tokenHash = hash(refreshToken);
        return sessionRepository.findSessionIdByRefreshTokenHash(tokenHash);
    }

    @Override
    public Optional<String> findUserCode(String refreshToken) {
        return findSessionId(refreshToken)
                .flatMap(sessionRepository::findUserCodeBySessionId);
    }

    @Override
    public void revoke(String refreshToken) {
        String tokenHash = hash(refreshToken);
        sessionRepository.revokeRefreshToken(tokenHash);
    }

    private String hash(String token) {

        try {

            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(
                    token.getBytes(StandardCharsets.UTF_8)
            );

            return Base64.getUrlEncoder()
                    .withoutPadding()
                    .encodeToString(hash);

        } catch (NoSuchAlgorithmException exception) {

            throw new TokenGenerationException(
                    "Hashing algorithm is not available.",
                    exception
            );
        }
    }
}
