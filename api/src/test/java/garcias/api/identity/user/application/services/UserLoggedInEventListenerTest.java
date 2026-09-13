package garcias.api.identity.user.application.services;

import garcias.api.identity.authentication.application.dto.events.UserLoggedInEvent;
import garcias.api.identity.user.domain.entities.User;
import garcias.api.identity.user.domain.enums.UserRole;
import garcias.api.identity.user.domain.enums.UserStatus;
import garcias.api.identity.user.domain.repositories.UserRepository;
import garcias.api.identity.user.domain.valueobjects.Password;
import garcias.api.identity.user.domain.valueobjects.UserCode;
import garcias.api.identity.user.domain.valueobjects.UserName;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserLoggedInEventListener Unit Tests")
class UserLoggedInEventListenerTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserLoggedInEventListener listener;

    @Test
    @DisplayName("Deve registrar login e salvar usuário quando encontrado")
    void shouldRecordLoginAndSaveUserWhenFound() {
        UserCode code = new UserCode("0001");
        User user = User.create(
                new UserName("User"), code, Password.fromHash("hash"),
                UserRole.ADMIN, UserStatus.ACTIVE
        );
        assertThat(user.getLastLoginAt()).isNull();

        when(userRepository.findByUserCode(code)).thenReturn(Optional.of(user));

        listener.handle(new UserLoggedInEvent("0001"));

        assertThat(user.getLastLoginAt()).isNotNull();
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Não deve fazer nada quando usuário não for encontrado")
    void shouldDoNothingWhenUserNotFound() {
        when(userRepository.findByUserCode(new UserCode("9999"))).thenReturn(Optional.empty());

        listener.handle(new UserLoggedInEvent("9999"));

        verify(userRepository, never()).save(any());
    }
}
