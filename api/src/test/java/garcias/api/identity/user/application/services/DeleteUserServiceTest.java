package garcias.api.identity.user.application.services;

import garcias.api.identity.user.application.dto.events.UserDeactivatedEvent;
import garcias.api.identity.user.domain.entities.User;
import garcias.api.identity.user.domain.enums.UserRole;
import garcias.api.identity.user.domain.enums.UserStatus;
import garcias.api.identity.user.domain.exceptions.UserNotFoundException;
import garcias.api.identity.user.domain.repositories.UserRepository;
import garcias.api.identity.user.domain.valueobjects.Password;
import garcias.api.identity.user.domain.valueobjects.UserCode;
import garcias.api.identity.user.domain.valueobjects.UserName;
import garcias.api.shared.exceptions.SuperAdminModificationNotAllowedException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DeleteUserService Unit Tests")
class DeleteUserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private DeleteUserService deleteUserService;

    @Test
    @DisplayName("Deve lançar UserNotFoundException quando usuário não for encontrado")
    void shouldThrowWhenUserNotFound() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> deleteUserService.execute(id))
                .isInstanceOf(UserNotFoundException.class);

        verify(userRepository, never()).delete(any());
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    @DisplayName("Deve lançar SuperAdminModificationNotAllowedException ao tentar deletar SUPER_ADMIN")
    void shouldThrowWhenDeletingSuperAdmin() {
        UUID id = UUID.randomUUID();
        User superAdmin = User.create(
                new UserName("Super"), new UserCode("0001"), Password.fromHash("hash"),
                UserRole.SUPER_ADMIN, UserStatus.ACTIVE
        );
        when(userRepository.findById(id)).thenReturn(Optional.of(superAdmin));

        assertThatThrownBy(() -> deleteUserService.execute(id))
                .isInstanceOf(SuperAdminModificationNotAllowedException.class);

        verify(userRepository, never()).delete(any());
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    @DisplayName("Deve deletar usuário comum com sucesso e publicar UserDeactivatedEvent")
    void shouldDeleteNormalUserSuccessfully() {
        UUID id = UUID.randomUUID();
        User normalUser = User.create(
                new UserName("Normal"), new UserCode("0002"), Password.fromHash("hash"),
                UserRole.ADMIN, UserStatus.ACTIVE
        );
        when(userRepository.findById(id)).thenReturn(Optional.of(normalUser));

        deleteUserService.execute(id);

        verify(eventPublisher).publishEvent(any(UserDeactivatedEvent.class));
        verify(userRepository).delete(normalUser);
    }
}
