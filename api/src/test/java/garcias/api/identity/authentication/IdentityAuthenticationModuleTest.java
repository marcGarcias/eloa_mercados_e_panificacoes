package garcias.api.identity.authentication;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import garcias.api.identity.authentication.domain.repositories.RefreshTokenRepository;
import garcias.api.identity.authentication.application.ports.UserAuthenticationPort;
import garcias.api.shared.security.application.PasswordHasher;

@ApplicationModuleTest
@DisplayName("Identity Authentication Module Integration Tests")
class IdentityAuthenticationModuleTest {

    @TestConfiguration
    static class TestConfig {
        @Bean
        public PasswordEncoder passwordEncoder() {
            return new PasswordEncoder() {
                @Override
                public String encode(CharSequence rawPassword) {
                    return rawPassword.toString();
                }
                @Override
                public boolean matches(CharSequence rawPassword, String encodedPassword) {
                    return rawPassword.toString().equals(encodedPassword);
                }
            };
        }
    }

    @MockitoBean
    private RefreshTokenRepository refreshTokenRepository;

    @MockitoBean
    private UserAuthenticationPort userAuthenticationPort;

    @MockitoBean
    private PasswordHasher passwordHasher;

    @Test
    @DisplayName("Verify that the Authentication module loads context successfully in isolation")
    void verifyModuleContextLoads() {
        // Test passes if the application context for this module successfully loads
    }
}
