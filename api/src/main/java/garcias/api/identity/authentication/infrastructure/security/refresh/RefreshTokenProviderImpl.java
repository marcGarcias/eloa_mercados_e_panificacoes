package garcias.api.identity.authentication.infrastructure.security.refresh;

import garcias.api.identity.authentication.application.security.RefreshTokenProvider;
import garcias.api.identity.authentication.domain.repositories.RefreshTokenRepository;
import garcias.api.identity.authentication.infrastructure.security.jwt.JwtProperties;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Optional;

@Service
public class RefreshTokenProviderImpl implements RefreshTokenProvider {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProperties jwtProperties;
    private final SecureRandom secureRandom;

    public RefreshTokenProviderImpl(
            RefreshTokenRepository refreshTokenRepository,
            JwtProperties jwtProperties
    ) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtProperties = jwtProperties;
        this.secureRandom = new SecureRandom();
    }

    @Override
    public String generate(String userCode) {

        byte[] tokenBytes = new byte[32];

        secureRandom.nextBytes(tokenBytes);

        String token = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(tokenBytes);

        String tokenHash = hash(token);

        refreshTokenRepository.save(
                tokenHash,
                userCode,
                jwtProperties.getRefreshTokenExpiration()
        );

        return token;
    }

    @Override
    public Optional<String> findUserCode(String refreshToken) {

        String tokenHash = hash(refreshToken);

        return refreshTokenRepository
                .findUserCodeByTokenHash(tokenHash);
    }

    @Override
    public void revoke(String refreshToken) {

        String tokenHash = hash(refreshToken);

        refreshTokenRepository.deleteByTokenHash(tokenHash);
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

            throw new IllegalStateException(
                    "Hashing algorithm is not available.",
                    exception
            );
        }
    }
}