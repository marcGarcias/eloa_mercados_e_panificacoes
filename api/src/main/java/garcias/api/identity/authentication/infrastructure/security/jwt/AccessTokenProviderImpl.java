package garcias.api.identity.authentication.infrastructure.security.jwt;

import garcias.api.identity.authentication.application.security.AccessTokenProvider;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class AccessTokenProviderImpl implements AccessTokenProvider {

    private final JwtProperties jwtProperties;
    private final SecretKey secretKey;

    public AccessTokenProviderImpl(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;

        this.secretKey = Keys.hmacShaKeyFor(
                jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8)
        );
    }

    @Override
    public String generate(String userCode, String role) {

        Date issuedAt = new Date();

        Date expiration = new Date(
                issuedAt.getTime() + jwtProperties.getAccessTokenExpiration()
        );

        return Jwts.builder()
                .subject(userCode)
                .claim("role", role)
                .issuer(jwtProperties.getIssuer())
                .issuedAt(issuedAt)
                .expiration(expiration)
                .signWith(secretKey)
                .compact();
    }

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

    public String extractUserCode(String token) {

        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public String extractRole(String token) {

        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("role", String.class);
    }
}