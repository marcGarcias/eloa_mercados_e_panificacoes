package garcias.api.identity.user.application.usecases;

import garcias.api.identity.user.application.dto.responses.UserResponse;
import garcias.api.identity.user.domain.entities.User;
import garcias.api.identity.user.domain.enums.UserRole;
import garcias.api.identity.user.domain.enums.UserStatus;
import garcias.api.identity.user.domain.exceptions.UserNotFoundException;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetCurrentUserUseCase Unit Tests")
class GetCurrentUserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private GetCurrentUserUseCase getCurrentUserUseCase;

    @Test
    @DisplayName("Deve retornar UserResponse quando usuário for encontrado por userCode")
    void shouldReturnUserResponseWhenFound() {
        User user = User.create(
                new UserName("Ana Clara"), new UserCode("0001"),
                Password.fromHash("hash"), UserRole.ADMIN, UserStatus.ACTIVE
        );

        when(userRepository.findByUserCode(new UserCode("0001"))).thenReturn(Optional.of(user));

        UserResponse response = getCurrentUserUseCase.execute("0001");

        assertThat(response).isNotNull();
        assertThat(response.name()).isEqualTo("Ana Clara");
        assertThat(response.userCode()).isEqualTo("0001");
        assertThat(response.role()).isEqualTo("ADMIN");
        assertThat(response.status()).isEqualTo("ACTIVE");
    }

    @Test
    @DisplayName("Deve lançar UserNotFoundException quando usuário não existir")
    void shouldThrowWhenUserNotFound() {
        when(userRepository.findByUserCode(new UserCode("9999"))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> getCurrentUserUseCase.execute("9999"))
                .isInstanceOf(UserNotFoundException.class);
    }
}
