package garcias.api.identity.authentication;

import garcias.api.identity.authentication.application.dto.requests.BootstrapUserRequest;
import garcias.api.identity.authentication.application.dto.responses.BootstrapUserResponse;
import garcias.api.identity.authentication.application.ports.UserAuthenticationPort;
import garcias.api.identity.authentication.application.services.BootstrapUserService;
import garcias.api.identity.authentication.domain.exceptions.BootstrapAlreadyCompletedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Bootstrap User Service Tests")
class BootstrapUserEventTest {

    @Mock
    private UserAuthenticationPort userAuthenticationPort;

    @InjectMocks
    private BootstrapUserService bootstrapUserService;

    private final String validAccessKey = "abc-123-xyz-!";
    private final String validCpf = "123.456.789-00";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(bootstrapUserService, "serverAccessKey", validAccessKey);
        ReflectionTestUtils.setField(bootstrapUserService, "serverCpf", validCpf);
    }

    @Test
    @DisplayName("Verify that a valid bootstrap request creates initial user successfully")
    void verifyBootstrapSuccess() {
        when(userAuthenticationPort.existsAnyUser()).thenReturn(false);
        when(userAuthenticationPort.createInitialUser("Admin", "adminPass123")).thenReturn("USR-12345");

        BootstrapUserRequest request = new BootstrapUserRequest("Admin", "adminPass123", validAccessKey, validCpf);
        BootstrapUserResponse response = bootstrapUserService.execute(request);

        assertNotNull(response);
        assertEquals("USR-12345", response.userCode());
        verify(userAuthenticationPort).createInitialUser("Admin", "adminPass123");
    }

    @Test
    @DisplayName("Verify that bootstrap throws an exception when users already exist")
    void verifyBootstrapThrowsExceptionWhenUserExists() {
        when(userAuthenticationPort.existsAnyUser()).thenReturn(true);

        BootstrapUserRequest request = new BootstrapUserRequest("Admin", "adminPass123", validAccessKey, validCpf);

        assertThrows(BootstrapAlreadyCompletedException.class, () -> {
            bootstrapUserService.execute(request);
        });
    }
}

