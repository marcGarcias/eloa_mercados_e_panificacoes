package garcias.api.identity.authentication.infrastructure.security.jwt;

import garcias.api.identity.authentication.application.security.AccessTokenManager;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class AccessTokenManagerImpl implements AccessTokenManager {

    private final JwtProperties jwtProperties;
    private final SecretKey secretKey;

    public AccessTokenManagerImpl(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;

        this.secretKey = Keys.hmacShaKeyFor(
                jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8)
        );
    }

    @Override
    public String generate(String userCode, String role, String status, String sessionId) {

        Date issuedAt = new Date();

        Date expiration = new Date(
                issuedAt.getTime() + jwtProperties.getAccessTokenExpiration()
        );

        var builder = Jwts.builder()
                .subject(userCode)
                .claim("role", role)
                .claim("status", status)
                .issuer(jwtProperties.getIssuer())
                .issuedAt(issuedAt)
                .expiration(expiration);

        if (sessionId != null && !sessionId.isBlank()) {
            builder.claim("sessionId", sessionId);
        }

        return builder
                .signWith(secretKey)
                .compact();
    }

    @Override
    public String generate(String userCode, String role, String status) {
        return generate(userCode, role, status, null);
    }

    @Override
    public boolean isValid(String token) {
        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);

            return true;

        } catch (Exception exception) {
            return false;
        }
    }

    @Override
    public String extractUserCode(String token) {

        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    @Override
    public String extractRole(String token) {

        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("role", String.class);
    }

    @Override
    public String extractStatus(String token) {

        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("status", String.class);
    }

    @Override
    public String extractSessionId(String token) {

        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("sessionId", String.class);
    }
}
