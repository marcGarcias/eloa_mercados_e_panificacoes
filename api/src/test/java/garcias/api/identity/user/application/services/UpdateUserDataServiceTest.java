package garcias.api.identity.user.application.services;

import garcias.api.identity.user.application.dto.events.UserDeactivatedEvent;
import garcias.api.identity.user.application.dto.requests.UpdateUserDataRequest;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UpdateUserDataService Unit Tests")
class UpdateUserDataServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private UpdateUserDataService updateUserDataService;

    private User createSampleUser(UserRole role, UserStatus status) {
        return User.create(
                new UserName("Nome Atual"),
                new UserCode("1234"),
                Password.fromHash("hash"),
                role,
                status
        );
    }

    @Test
    @DisplayName("Deve lançar UserNotFoundException quando usuário não for encontrado")
    void shouldThrowWhenUserNotFound() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        UpdateUserDataRequest request = new UpdateUserDataRequest("Novo Nome", null, null);

        assertThatThrownBy(() -> updateUserDataService.execute(id, request))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    @DisplayName("Deve atualizar nome com sucesso")
    void shouldUpdateNameSuccessfully() {
        UUID id = UUID.randomUUID();
        User user = createSampleUser(UserRole.ADMIN, UserStatus.ACTIVE);
        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        UpdateUserDataRequest request = new UpdateUserDataRequest("Novo Nome", null, null);

        updateUserDataService.execute(id, request);

        assertThat(user.getName().value()).isEqualTo("Novo Nome");
        verify(userRepository).save(user);
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    @DisplayName("Deve atualizar role permitida entre não-superadmin com sucesso")
    void shouldUpdateAllowedRoleSuccessfully() {
        UUID id = UUID.randomUUID();
        User user = createSampleUser(UserRole.ADMIN, UserStatus.ACTIVE);
        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        UpdateUserDataRequest request = new UpdateUserDataRequest(null, UserRole.ADMIN, null);

        updateUserDataService.execute(id, request);

        assertThat(user.getRole()).isEqualTo(UserRole.ADMIN);
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Deve lançar SuperAdminModificationNotAllowedException ao tentar promover usuário para SUPER_ADMIN")
    void shouldThrowWhenPromotingToSuperAdmin() {
        UUID id = UUID.randomUUID();
        User user = createSampleUser(UserRole.ADMIN, UserStatus.ACTIVE);
        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        UpdateUserDataRequest request = new UpdateUserDataRequest(null, UserRole.SUPER_ADMIN, null);

        assertThatThrownBy(() -> updateUserDataService.execute(id, request))
                .isInstanceOf(SuperAdminModificationNotAllowedException.class);

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve permitir SUPER_ADMIN manter role SUPER_ADMIN")
    void shouldAllowSuperAdminToKeepSuperAdminRole() {
        UUID id = UUID.randomUUID();
        User user = createSampleUser(UserRole.SUPER_ADMIN, UserStatus.ACTIVE);
        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        UpdateUserDataRequest request = new UpdateUserDataRequest(null, UserRole.SUPER_ADMIN, null);

        updateUserDataService.execute(id, request);

        assertThat(user.getRole()).isEqualTo(UserRole.SUPER_ADMIN);
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Deve lançar SuperAdminModificationNotAllowedException ao tentar despromover SUPER_ADMIN")
    void shouldThrowWhenDemotingSuperAdmin() {
        UUID id = UUID.randomUUID();
        User user = createSampleUser(UserRole.SUPER_ADMIN, UserStatus.ACTIVE);
        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        UpdateUserDataRequest request = new UpdateUserDataRequest(null, UserRole.ADMIN, null);

        assertThatThrownBy(() -> updateUserDataService.execute(id, request))
                .isInstanceOf(SuperAdminModificationNotAllowedException.class);

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar SuperAdminModificationNotAllowedException ao tentar inativar SUPER_ADMIN")
    void shouldThrowWhenDeactivatingSuperAdmin() {
        UUID id = UUID.randomUUID();
        User user = createSampleUser(UserRole.SUPER_ADMIN, UserStatus.ACTIVE);
        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        UpdateUserDataRequest request = new UpdateUserDataRequest(null, null, UserStatus.INACTIVE);

        assertThatThrownBy(() -> updateUserDataService.execute(id, request))
                .isInstanceOf(SuperAdminModificationNotAllowedException.class);

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve permitir SUPER_ADMIN manter status ACTIVE")
    void shouldAllowSuperAdminToStayActive() {
        UUID id = UUID.randomUUID();
        User user = createSampleUser(UserRole.SUPER_ADMIN, UserStatus.ACTIVE);
        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        UpdateUserDataRequest request = new UpdateUserDataRequest(null, null, UserStatus.ACTIVE);

        updateUserDataService.execute(id, request);

        assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);
        verify(userRepository).save(user);
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    @DisplayName("Deve inativar usuário e publicar UserDeactivatedEvent")
    void shouldDeactivateUserAndPublishEvent() {
        UUID id = UUID.randomUUID();
        User user = createSampleUser(UserRole.ADMIN, UserStatus.ACTIVE);
        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        UpdateUserDataRequest request = new UpdateUserDataRequest(null, null, UserStatus.INACTIVE);

        updateUserDataService.execute(id, request);

        assertThat(user.getStatus()).isEqualTo(UserStatus.INACTIVE);
        verify(userRepository).save(user);
        verify(eventPublisher).publishEvent(any(UserDeactivatedEvent.class));
    }

    @Test
    @DisplayName("Deve ativar usuário inativo sem publicar evento de desativação")
    void shouldActivateUserWithoutDeactivationEvent() {
        UUID id = UUID.randomUUID();
        User user = createSampleUser(UserRole.ADMIN, UserStatus.INACTIVE);
        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        UpdateUserDataRequest request = new UpdateUserDataRequest(null, null, UserStatus.ACTIVE);

        updateUserDataService.execute(id, request);

        assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);
        verify(userRepository).save(user);
        verify(eventPublisher, never()).publishEvent(any());
    }
}
