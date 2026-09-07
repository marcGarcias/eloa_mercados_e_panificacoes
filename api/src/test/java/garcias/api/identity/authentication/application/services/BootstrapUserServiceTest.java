package garcias.api.identity.authentication.application.services;

import garcias.api.identity.authentication.application.dto.requests.BootstrapUserRequest;
import garcias.api.identity.authentication.application.dto.responses.BootstrapUserResponse;
import garcias.api.identity.authentication.application.ports.UserAuthenticationPort;
import garcias.api.identity.authentication.domain.exceptions.BootstrapAlreadyCompletedException;
import garcias.api.identity.authentication.domain.exceptions.InvalidSetupAccessKeyException;
import garcias.api.identity.authentication.domain.exceptions.InvalidSetupCpfException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BootstrapUserService Security Unit Tests")
class BootstrapUserServiceTest {

    @Mock
    private UserAuthenticationPort userAuthenticationPort;

    private BootstrapUserService bootstrapUserService;

    private final String validServerKey = "AAA-111-BBB-!";
    private final String validServerCpf = "123.456.789-09";

    @BeforeEach
    void setUp() {
        bootstrapUserService = new BootstrapUserService(userAuthenticationPort);
        ReflectionTestUtils.setField(bootstrapUserService, "serverAccessKey", validServerKey);
        ReflectionTestUtils.setField(bootstrapUserService, "serverCpf", validServerCpf);
    }

    @Test
    @DisplayName("Should bootstrap initial admin successfully with valid credentials, access key and CPF")
    void shouldBootstrapSuccessfully() {
        BootstrapUserRequest request = new BootstrapUserRequest(
                "Administrador",
                "12345678",
                validServerKey,
                "12345678909"
        );

        when(userAuthenticationPort.existsAnyUser()).thenReturn(false);
        when(userAuthenticationPort.createInitialUser("Administrador", "12345678")).thenReturn("0001");

        BootstrapUserResponse response = bootstrapUserService.execute(request);

        assertNotNull(response);
        assertEquals("0001", response.userCode());
        verify(userAuthenticationPort).createInitialUser("Administrador", "12345678");
    }

    @Test
    @DisplayName("Should throw BootstrapAlreadyCompletedException when users already exist in database")
    void shouldThrowExceptionWhenUsersAlreadyExist() {
        BootstrapUserRequest request = new BootstrapUserRequest(
                "Admin",
                "12345678",
                validServerKey,
                validServerCpf
        );

        when(userAuthenticationPort.existsAnyUser()).thenReturn(true);

        assertThrows(BootstrapAlreadyCompletedException.class, () -> bootstrapUserService.execute(request));
        verify(userAuthenticationPort, never()).createInitialUser(anyString(), anyString());
    }

    @Test
    @DisplayName("Should throw InvalidSetupCpfException when CPF is wrong or null")
    void shouldThrowExceptionWhenCpfIsWrong() {
        BootstrapUserRequest request = new BootstrapUserRequest(
                "Admin",
                "12345678",
                validServerKey,
                "999.999.999-99"
        );

        when(userAuthenticationPort.existsAnyUser()).thenReturn(false);

        assertThrows(InvalidSetupCpfException.class, () -> bootstrapUserService.execute(request));
        verify(userAuthenticationPort, never()).createInitialUser(anyString(), anyString());
    }

    @Test
    @DisplayName("Should throw InvalidSetupAccessKeyException when access key format does not match regex")
    void shouldThrowExceptionWhenAccessKeyFormatIsInvalid() {
        BootstrapUserRequest request = new BootstrapUserRequest(
                "Admin",
                "12345678",
                "invalid-format",
                validServerCpf
        );

        when(userAuthenticationPort.existsAnyUser()).thenReturn(false);

        assertThrows(InvalidSetupAccessKeyException.class, () -> bootstrapUserService.execute(request));
    }

    @Test
    @DisplayName("Should throw InvalidSetupAccessKeyException when access key does not match server key")
    void shouldThrowExceptionWhenAccessKeyDoesNotMatchServer() {
        BootstrapUserRequest request = new BootstrapUserRequest(
                "Admin",
                "12345678",
                "CCC-333-DDD-#",
                validServerCpf
        );

        when(userAuthenticationPort.existsAnyUser()).thenReturn(false);

        assertThrows(InvalidSetupAccessKeyException.class, () -> bootstrapUserService.execute(request));
        verify(userAuthenticationPort, never()).createInitialUser(anyString(), anyString());
    }
}
