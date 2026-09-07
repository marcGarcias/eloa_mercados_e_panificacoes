package garcias.api.identity.authentication.infrastructure.security.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JwtTokenService Security Unit Tests")
class JwtTokenServiceTest {

    private final String secret = "v7GFd2Z5qCs/kyCdCKX21Eo5Ver3Ii9DdOWhqk7hNCtNfhjKB9LtNBl+CWZiwPK+CobVHOeN1olBOhFNCXa4Kg==";
    private final String differentSecret = "differentSecretKey1234567890123456789012345678901234567890123456789012345678901234567890";
    private JwtProperties jwtProperties;
    private JwtTokenService jwtTokenService;

    @BeforeEach
    void setUp() {
        jwtProperties = new JwtProperties();
        jwtProperties.setSecret(secret);
        jwtProperties.setAccessTokenExpiration(180000); // 3 minutes
        jwtProperties.setIssuer("garcias-api");
        jwtTokenService = new JwtTokenService(jwtProperties);
    }

    @Test
    @DisplayName("Should generate valid JWT access token and extract subject and role")
    void shouldGenerateAndExtractTokenClaims() {
        String token = jwtTokenService.generateAccessToken("0001", "SUPER_ADMIN", "ACTIVE");

        assertNotNull(token);
        assertTrue(jwtTokenService.isValid(token));
        assertEquals("0001", jwtTokenService.extractUserCode(token));
        assertEquals("SUPER_ADMIN", jwtTokenService.extractRole(token));
    }

    @Test
    @DisplayName("Should return false when validating a token signed with a different secret key (Tampering)")
    void shouldRejectTokenSignedWithDifferentSecret() {
        SecretKey attackerKey = Keys.hmacShaKeyFor(differentSecret.getBytes(StandardCharsets.UTF_8));
        String forgedToken = Jwts.builder()
                .subject("0001")
                .claim("role", "SUPER_ADMIN")
                .issuer("garcias-api")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 60000))
                .signWith(attackerKey)
                .compact();

        assertFalse(jwtTokenService.isValid(forgedToken));
    }

    @Test
    @DisplayName("Should return false when validating an expired token")
    void shouldRejectExpiredToken() {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        String expiredToken = Jwts.builder()
                .subject("0001")
                .claim("role", "SUPER_ADMIN")
                .issuer("garcias-api")
                .issuedAt(new Date(System.currentTimeMillis() - 100000))
                .expiration(new Date(System.currentTimeMillis() - 1000)) // expired in the past
                .signWith(key)
                .compact();

        assertFalse(jwtTokenService.isValid(expiredToken));
    }

    @Test
    @DisplayName("Should return false when validating a malformed token string")
    void shouldRejectMalformedToken() {
        assertFalse(jwtTokenService.isValid("not.a.valid.jwt"));
        assertFalse(jwtTokenService.isValid(""));
        assertFalse(jwtTokenService.isValid(null));
    }
}
