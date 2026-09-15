package garcias.api.identity.user.application.services;

import garcias.api.identity.authentication.domain.exceptions.InvalidSetupAccessKeyException;
import garcias.api.identity.authentication.domain.exceptions.InvalidSetupCpfException;
import garcias.api.identity.user.application.dto.events.UserPasswordChangedEvent;
import garcias.api.identity.user.application.dto.requests.ChangePasswordRequest;
import garcias.api.identity.user.domain.entities.User;
import garcias.api.identity.user.domain.enums.UserRole;
import garcias.api.identity.user.domain.enums.UserStatus;
import garcias.api.identity.user.domain.exceptions.InvalidUserPasswordException;
import garcias.api.identity.user.domain.exceptions.UserNotFoundException;
import garcias.api.identity.user.domain.repositories.UserRepository;
import garcias.api.identity.user.domain.valueobjects.Password;
import garcias.api.identity.user.domain.valueobjects.UserCode;
import garcias.api.identity.user.domain.valueobjects.UserName;
import garcias.api.shared.security.application.PasswordHasher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ChangePasswordService Unit Tests")
class ChangePasswordServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordHasher passwordHasher;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private ChangePasswordService changePasswordService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(changePasswordService, "serverCpf", "123.456.789-09");
        ReflectionTestUtils.setField(changePasswordService, "serverAccessKey", "AAA-111-BBB-!");
    }

    @Test
    @DisplayName("Deve lançar UserNotFoundException quando usuário não for encontrado")
    void shouldThrowWhenUserNotFound() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        ChangePasswordRequest request = new ChangePasswordRequest("NovaSenha123!");

        assertThatThrownBy(() -> changePasswordService.execute(id, request))
                .isInstanceOf(UserNotFoundException.class);

        verify(userRepository, never()).save(any());
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    @DisplayName("Deve lançar InvalidSetupCpfException quando SUPER_ADMIN alterar senha com CPF incorreto ou nulo")
    void shouldThrowWhenSuperAdminHasInvalidCpf() {
        UUID id = UUID.randomUUID();
        User owner = User.create(
                new UserName("Proprietário"), new UserCode("0001"),
                Password.fromHash("current_hash"), UserRole.SUPER_ADMIN, UserStatus.ACTIVE
        );
        when(userRepository.findById(id)).thenReturn(Optional.of(owner));

        ChangePasswordRequest request = new ChangePasswordRequest("NovaSenha123!", "AAA-111-BBB-!", "000.000.000-00");

        assertThatThrownBy(() -> changePasswordService.execute(id, request))
                .isInstanceOf(InvalidSetupCpfException.class);

        verify(userRepository, never()).save(any());
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    @DisplayName("Deve lançar InvalidSetupAccessKeyException quando SUPER_ADMIN alterar senha com AccessKey incorreta")
    void shouldThrowWhenSuperAdminHasInvalidAccessKey() {
        UUID id = UUID.randomUUID();
        User owner = User.create(
                new UserName("Proprietário"), new UserCode("0001"),
                Password.fromHash("current_hash"), UserRole.SUPER_ADMIN, UserStatus.ACTIVE
        );
        when(userRepository.findById(id)).thenReturn(Optional.of(owner));

        ChangePasswordRequest request = new ChangePasswordRequest("NovaSenha123!", "WRONG-KEY", "12345678909");

        assertThatThrownBy(() -> changePasswordService.execute(id, request))
                .isInstanceOf(InvalidSetupAccessKeyException.class);

        verify(userRepository, never()).save(any());
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    @DisplayName("Deve alterar senha do SUPER_ADMIN com sucesso quando CPF e AccessKey estiverem corretos")
    void shouldChangePasswordForSuperAdminSuccessfully() {
        UUID id = UUID.randomUUID();
        User owner = User.create(
                new UserName("Proprietário"), new UserCode("0001"),
                Password.fromHash("current_hash"), UserRole.SUPER_ADMIN, UserStatus.ACTIVE
        );
        when(userRepository.findById(id)).thenReturn(Optional.of(owner));
        when(passwordHasher.matches("NovaSenhaForte456!", "current_hash")).thenReturn(false);
        when(passwordHasher.hash("NovaSenhaForte456!")).thenReturn("new_hash");

        ChangePasswordRequest request = new ChangePasswordRequest("NovaSenhaForte456!", "AAA-111-BBB-!", "12345678909");

        changePasswordService.execute(id, request);

        assertThat(owner.getPassword().value()).isEqualTo("new_hash");
        verify(userRepository).save(owner);
        verify(eventPublisher).publishEvent(any(UserPasswordChangedEvent.class));
    }

    @Test
    @DisplayName("Deve lançar InvalidUserPasswordException quando nova senha for idêntica à senha atual")
    void shouldThrowWhenNewPasswordIsSameAsCurrent() {
        UUID id = UUID.randomUUID();
        User user = User.create(
                new UserName("User"), new UserCode("0001"),
                Password.fromHash("current_hash"), UserRole.ADMIN, UserStatus.ACTIVE
        );
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(passwordHasher.matches("SenhaIgual123!", "current_hash")).thenReturn(true);

        ChangePasswordRequest request = new ChangePasswordRequest("SenhaIgual123!");

        assertThatThrownBy(() -> changePasswordService.execute(id, request))
                .isInstanceOf(InvalidUserPasswordException.class);

        verify(userRepository, never()).save(any());
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    @DisplayName("Deve alterar senha com sucesso gerando novo hash e publicando UserPasswordChangedEvent para usuário comum")
    void shouldChangePasswordSuccessfully() {
        UUID id = UUID.randomUUID();
        User user = User.create(
                new UserName("User"), new UserCode("0001"),
                Password.fromHash("current_hash"), UserRole.ADMIN, UserStatus.ACTIVE
        );
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(passwordHasher.matches("NovaSenhaForte456!", "current_hash")).thenReturn(false);
        when(passwordHasher.hash("NovaSenhaForte456!")).thenReturn("new_hash");

        ChangePasswordRequest request = new ChangePasswordRequest("NovaSenhaForte456!");

        changePasswordService.execute(id, request);

        assertThat(user.getPassword().value()).isEqualTo("new_hash");
        verify(userRepository).save(user);
        verify(eventPublisher).publishEvent(any(UserPasswordChangedEvent.class));
    }
}
