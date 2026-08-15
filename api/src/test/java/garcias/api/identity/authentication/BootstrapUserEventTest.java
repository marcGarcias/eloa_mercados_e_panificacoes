package garcias.api.identity.authentication;

import garcias.api.identity.authentication.application.dto.events.BootstrapUserRequestedEvent;
import garcias.api.identity.authentication.application.dto.requests.BootstrapUserRequest;
import garcias.api.identity.authentication.application.ports.UserAuthenticationPort;
import garcias.api.identity.authentication.application.services.BootstrapUserService;
import garcias.api.identity.authentication.domain.exceptions.BootstrapAlreadyCompletedException;
import garcias.api.identity.authentication.domain.repositories.RefreshTokenRepository;
import garcias.api.shared.security.application.PasswordHasher;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.springframework.modulith.test.AssertablePublishedEvents;

import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ApplicationModuleTest
@DisplayName("Bootstrap User Event Tests")
class BootstrapUserEventTest {

    @MockitoBean
    private UserAuthenticationPort userAuthenticationPort;

    @MockitoBean
    private RefreshTokenRepository refreshTokenRepository;

    @MockitoBean
    private PasswordHasher passwordHasher;

    @Autowired
    private BootstrapUserService bootstrapUserService;

    @Test
    @DisplayName("Verify that a valid bootstrap request publishes the BootstrapUserRequestedEvent")
    void verifyBootstrapEventPublished(AssertablePublishedEvents events) {
        when(userAuthenticationPort.existsAnyUser()).thenReturn(false);

        BootstrapUserRequest request = new BootstrapUserRequest("Admin", "adminPass");
        bootstrapUserService.execute(request);

        assertThat(events.ofType(BootstrapUserRequestedEvent.class))
                .hasSize(1)
                .element(0)
                .satisfies(event -> {
                    assertThat(event.name()).isEqualTo("Admin");
                    assertThat(event.password()).isEqualTo("adminPass");
                });
    }

    @Test
    @DisplayName("Verify that bootstrap throws an exception when users already exist")
    void verifyBootstrapThrowsExceptionWhenUserExists() {
        when(userAuthenticationPort.existsAnyUser()).thenReturn(true);

        BootstrapUserRequest request = new BootstrapUserRequest("Admin", "adminPass");

        assertThrows(BootstrapAlreadyCompletedException.class, () -> {
            bootstrapUserService.execute(request);
        });
    }
}
