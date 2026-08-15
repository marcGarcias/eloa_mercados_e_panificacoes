package garcias.api.identity.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.modulith.test.ApplicationModuleTest;
import garcias.api.identity.user.domain.repositories.UserRepository;
import garcias.api.shared.security.application.PasswordHasher;

@ApplicationModuleTest
@DisplayName("Identity User Module Integration Tests")
class IdentityUserModuleTest {

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
