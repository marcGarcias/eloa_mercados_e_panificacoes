package garcias.api.identity.user;

import garcias.api.identity.user.application.dto.events.UserDeactivatedEvent;
import garcias.api.identity.user.application.services.DeleteUserService;
import garcias.api.identity.user.domain.entities.User;
import garcias.api.identity.user.domain.enums.UserRole;
import garcias.api.identity.user.domain.enums.UserStatus;
import garcias.api.identity.user.domain.repositories.UserRepository;
import garcias.api.identity.user.domain.valueobjects.Password;
import garcias.api.identity.user.domain.valueobjects.UserCode;
import garcias.api.identity.user.domain.valueobjects.UserName;
import garcias.api.shared.security.application.PasswordHasher;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.springframework.modulith.test.AssertablePublishedEvents;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import garcias.api.identity.authentication.domain.repositories.RefreshTokenRepository;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.springframework.test.context.TestPropertySource;

@ApplicationModuleTest
@TestPropertySource(properties = {
        "spring.data.redis.repositories.enabled=false"
})
@DisplayName("User Deactivated Event Tests")
class UserDeactivatedEventTest {

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private PasswordHasher passwordHasher;

    @MockitoBean
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private DeleteUserService deleteUserService;

    @Test
    @DisplayName("Verify that deleting a user publishes a UserDeactivatedEvent")
    void verifyUserDeactivatedEventPublished(AssertablePublishedEvents events) {
        UUID userId = UUID.randomUUID();
        String userCode = "000001";

        User mockUser = User.create(
                new UserName("Test User"),
                new UserCode(userCode),
                new Password("hashedpassword"),
                UserRole.ADMIN,
                UserStatus.ACTIVE
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

        deleteUserService.execute(userId);

        assertThat(events.ofType(UserDeactivatedEvent.class))
                .hasSize(1)
                .element(0)
                .extracting(UserDeactivatedEvent::userCode)
                .isEqualTo(userCode);
    }
}
