package garcias.api.identity.user.application.services;

import garcias.api.identity.user.application.dto.requests.CreateUserRequest;
import garcias.api.identity.user.domain.entities.User;
import garcias.api.identity.user.domain.enums.UserRole;
import garcias.api.identity.user.domain.enums.UserStatus;
import garcias.api.identity.user.domain.repositories.UserRepository;
import garcias.api.identity.user.domain.services.UserCodeProvider;
import garcias.api.identity.user.domain.valueobjects.UserCode;
import garcias.api.shared.exceptions.SuperAdminAlreadyExistsException;
import garcias.api.shared.security.application.PasswordHasher;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreateUserService Unit Tests")
class CreateUserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserCodeProvider userCodeProvider;

    @Mock
    private PasswordHasher passwordHasher;

    @InjectMocks
    private CreateUserService createUserService;

    @Test
    @DisplayName("Deve criar usuário com sucesso tratando colisão inicial de UserCode")
    void shouldCreateUserHandlingCodeCollision() {
        CreateUserRequest request = new CreateUserRequest(
                "Lucas Ferreira",
                "SenhaSegura123!",
                UserRole.ADMIN,
                UserStatus.ACTIVE
        );

        UserCode collidedCode = new UserCode("0001");
        UserCode uniqueCode = new UserCode("0002");

        when(userCodeProvider.generate()).thenReturn(collidedCode, uniqueCode);
        when(userRepository.existsByCode(collidedCode)).thenReturn(true);
        when(userRepository.existsByCode(uniqueCode)).thenReturn(false);

        when(passwordHasher.hash("SenhaSegura123!")).thenReturn("hashed_password");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User result = createUserService.execute(request);

        assertThat(result).isNotNull();
        assertThat(result.getName().value()).isEqualTo("Lucas Ferreira");
        assertThat(result.getUserCode()).isEqualTo(uniqueCode);
        assertThat(result.getPassword().value()).isEqualTo("hashed_password");
        assertThat(result.getRole()).isEqualTo(UserRole.ADMIN);

        verify(userCodeProvider, times(2)).generate();
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Deve lançar SuperAdminAlreadyExistsException ao tentar criar SUPER_ADMIN quando um já existir")
    void shouldThrowWhenSuperAdminAlreadyExists() {
        CreateUserRequest request = new CreateUserRequest(
                "Super Admin 2",
                "SenhaSegura123!",
                UserRole.SUPER_ADMIN,
                UserStatus.ACTIVE
        );

        when(userRepository.existsByRole(UserRole.SUPER_ADMIN)).thenReturn(true);

        assertThatThrownBy(() -> createUserService.execute(request))
                .isInstanceOf(SuperAdminAlreadyExistsException.class);

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve permitir criar SUPER_ADMIN se ainda não existir nenhum no repositório")
    void shouldCreateSuperAdminWhenNoneExists() {
        CreateUserRequest request = new CreateUserRequest(
                "Primeiro Super Admin",
                "SenhaSegura123!",
                UserRole.SUPER_ADMIN,
                UserStatus.ACTIVE
        );

        UserCode code = new UserCode("0001");
        when(userRepository.existsByRole(UserRole.SUPER_ADMIN)).thenReturn(false);
        when(userCodeProvider.generate()).thenReturn(code);
        when(userRepository.existsByCode(code)).thenReturn(false);
        when(passwordHasher.hash("SenhaSegura123!")).thenReturn("hashed_pw");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User result = createUserService.execute(request);

        assertThat(result.getRole()).isEqualTo(UserRole.SUPER_ADMIN);
        verify(userRepository).save(any(User.class));
    }
}
