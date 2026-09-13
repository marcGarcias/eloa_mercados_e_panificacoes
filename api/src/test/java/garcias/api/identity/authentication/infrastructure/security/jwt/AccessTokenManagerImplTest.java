package garcias.api.identity.authentication.infrastructure.security.jwt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Testes Unitários do AccessTokenManagerImpl")
class AccessTokenManagerImplTest {

    private AccessTokenManagerImpl accessTokenManager;
    private JwtProperties jwtProperties;

    @BeforeEach
    void setUp() {
        jwtProperties = new JwtProperties();
        jwtProperties.setSecret("chave-secreta-muito-segura-com-pelo-menos-256-bits-de-comprimento-para-jwt!");
        jwtProperties.setIssuer("test-issuer");
        jwtProperties.setAccessTokenExpiration(3600000L); // 1h
        jwtProperties.setRefreshTokenExpiration(604800000L); // 7d

        accessTokenManager = new AccessTokenManagerImpl(jwtProperties);
    }

    @Test
    @DisplayName("Deve gerar token válido e extrair claims corretamente")
    void shouldGenerateAndExtractClaims() {
        String token = accessTokenManager.generate("1001", "SUPER_ADMIN", "ACTIVE");

        assertThat(token).isNotBlank();
        assertThat(accessTokenManager.isValid(token)).isTrue();
        assertThat(accessTokenManager.extractUserCode(token)).isEqualTo("1001");
        assertThat(accessTokenManager.extractRole(token)).isEqualTo("SUPER_ADMIN");
        assertThat(accessTokenManager.extractStatus(token)).isEqualTo("ACTIVE");
    }

    @Test
    @DisplayName("Deve retornar false para token inválido ou malformado")
    void shouldReturnFalseForInvalidToken() {
        assertThat(accessTokenManager.isValid("token.invalido.xyz")).isFalse();
        assertThat(accessTokenManager.isValid("")).isFalse();
        assertThat(accessTokenManager.isValid(null)).isFalse();
    }
}
