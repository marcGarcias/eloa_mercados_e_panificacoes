package garcias.api.identity.authentication.application.services;

import garcias.api.identity.authentication.application.usecases.LogoutUseCase;
import garcias.api.identity.user.application.dto.events.UserPasswordChangedEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserPasswordChangedEventListener Unit Tests")
class UserPasswordChangedEventListenerTest {

    @Mock
    private LogoutUseCase logoutUseCase;

    @InjectMocks
    private UserPasswordChangedEventListener listener;

    @Test
    @DisplayName("Deve revogar sessões ativas do usuário ao receber UserPasswordChangedEvent")
    void shouldRevokeSessionsWhenPasswordChanged() {
        UserPasswordChangedEvent event = new UserPasswordChangedEvent("0001");

        listener.onUserPasswordChanged(event);

        verify(logoutUseCase).execute("0001");
    }
}
