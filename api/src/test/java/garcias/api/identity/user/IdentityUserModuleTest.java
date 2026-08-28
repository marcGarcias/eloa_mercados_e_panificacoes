package garcias.api.identity.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import garcias.api.identity.user.domain.repositories.UserRepository;
import garcias.api.shared.security.application.PasswordHasher;

@ApplicationModuleTest
@DisplayName("Identity User Module Integration Tests")
class IdentityUserModuleTest {

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
    private UserRepository userRepository;

    @MockitoBean
    private PasswordHasher passwordHasher;

    @Test
    @DisplayName("Verify that the User module loads context successfully in isolation")
    void verifyModuleContextLoads() {
        // Test passes if the application context for this module successfully loads
    }
}
