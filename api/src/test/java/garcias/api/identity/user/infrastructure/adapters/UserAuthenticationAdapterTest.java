package garcias.api.identity.user.infrastructure.adapters;

import garcias.api.identity.authentication.application.dto.results.UserAuthenticationDto;
import garcias.api.identity.user.application.dto.requests.CreateUserRequest;
import garcias.api.identity.user.application.usecases.CreateUserUseCase;
import garcias.api.identity.user.domain.entities.User;
import garcias.api.identity.user.domain.enums.UserRole;
import garcias.api.identity.user.domain.enums.UserStatus;
import garcias.api.identity.user.domain.repositories.UserRepository;
import garcias.api.identity.user.domain.valueobjects.Password;
import garcias.api.identity.user.domain.valueobjects.UserCode;
import garcias.api.identity.user.domain.valueobjects.UserId;
import garcias.api.identity.user.domain.valueobjects.UserName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários do UserAuthenticationAdapter")
class UserAuthenticationAdapterTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private CreateUserUseCase createUserUseCase;

    private UserAuthenticationAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new UserAuthenticationAdapter(userRepository, createUserUseCase);
    }

    @Test
    @DisplayName("Deve retornar UserAuthenticationDto quando usuário existir")
    void shouldReturnDtoWhenUserExists() {
        User user = User.create(
                new UserName("Admin Master"),
                new UserCode("1001"),
                new Password("$argon2id$v=19$m=65536,t=3,p=1$fakehash"),
                UserRole.SUPER_ADMIN,
                UserStatus.ACTIVE
        );

        when(userRepository.findByUserCode(new UserCode("1001"))).thenReturn(Optional.of(user));

        Optional<UserAuthenticationDto> result = adapter.findByUserCode("1001");

        assertThat(result).isPresent();
        assertThat(result.get().userCode()).isEqualTo("1001");
        assertThat(result.get().role()).isEqualTo("SUPER_ADMIN");
        assertThat(result.get().status()).isEqualTo("ACTIVE");
    }

    @Test
    @DisplayName("Deve retornar Optional.empty() quando usuário não existir")
    void shouldReturnEmptyWhenUserNotFound() {
        when(userRepository.findByUserCode(new UserCode("9999"))).thenReturn(Optional.empty());

        Optional<UserAuthenticationDto> result = adapter.findByUserCode("9999");

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Deve checar se existe qualquer usuário no sistema")
    void shouldCheckExistsAnyUser() {
        when(userRepository.existsAnyUser()).thenReturn(true);
        assertThat(adapter.existsAnyUser()).isTrue();

        when(userRepository.existsAnyUser()).thenReturn(false);
        assertThat(adapter.existsAnyUser()).isFalse();
    }

    @Test
    @DisplayName("Deve criar usuário inicial e retornar seu código")
    void shouldCreateInitialUser() {
        User user = User.create(
                new UserName("Root User"),
                new UserCode("1001"),
                new Password("hashed"),
                UserRole.SUPER_ADMIN,
                UserStatus.ACTIVE
        );

        when(createUserUseCase.execute(any(CreateUserRequest.class))).thenReturn(user);

        String code = adapter.createInitialUser("Root User", "SenhaForte@123");

        assertThat(code).isEqualTo("1001");
        verify(createUserUseCase).execute(argThat(req ->
                req.name().equals("Root User") &&
                req.role() == UserRole.SUPER_ADMIN &&
                req.status() == UserStatus.ACTIVE
        ));
    }
}
