package garcias.api.identity.user.infrastructure.presentation.admin;

import garcias.api.identity.user.application.dto.requests.ChangePasswordRequest;
import garcias.api.identity.user.application.dto.requests.CreateUserRequest;
import garcias.api.identity.user.application.dto.requests.UpdateUserDataRequest;
import garcias.api.identity.user.application.dto.responses.UserResponse;
import garcias.api.identity.user.application.usecases.ChangePasswordUseCase;
import garcias.api.identity.user.application.usecases.CreateUserUseCase;
import garcias.api.identity.user.application.usecases.DeleteUserUseCase;
import garcias.api.identity.user.application.usecases.ListUsersUseCase;
import garcias.api.identity.user.application.usecases.UpdateUserDataUseCase;
import garcias.api.identity.user.domain.enums.UserRole;
import garcias.api.identity.user.domain.enums.UserStatus;
import garcias.api.shared.exceptions.SuperAdminCreationNotAllowedException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários dos Controllers de Usuários Administrativos")
class UserControllersTest {

    @Nested
    @DisplayName("UserAdminController")
    class UserAdminControllerTests {
        @Mock
        private ListUsersUseCase listUsersUseCase;

        @Test
        @DisplayName("Deve listar usuários paginados")
        void shouldListUsers() {
            UserAdminController controller = new UserAdminController(listUsersUseCase);
            Pageable pageable = PageRequest.of(0, 10);
            UserResponse userResponse = new UserResponse(
                    "550e8400-e29b-41d4-a716-446655440000", "1001", "Admin", "SUPER_ADMIN", "ACTIVE", null
            );
            Page<UserResponse> page = new PageImpl<>(List.of(userResponse));
            when(listUsersUseCase.execute(pageable)).thenReturn(page);

            ResponseEntity<Page<UserResponse>> response = controller.listUsers(pageable);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getTotalElements()).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("CreateUserController")
    class CreateUserControllerTests {
        @Mock
        private CreateUserUseCase createUserUseCase;

        @Test
        @DisplayName("Deve criar usuário comum com sucesso e retornar 201 Created")
        void shouldCreateUser() {
            CreateUserController controller = new CreateUserController(createUserUseCase);
            CreateUserRequest request = new CreateUserRequest("João Silva", "Senha@1234", UserRole.ADMIN, UserStatus.ACTIVE);

            ResponseEntity<Void> response = controller.createUser(request);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(response.getHeaders().getLocation()).isEqualTo(URI.create("/api/admin/users"));
            verify(createUserUseCase).execute(request);
        }

        @Test
        @DisplayName("Deve lançar SuperAdminCreationNotAllowedException se tentar criar SUPER_ADMIN")
        void shouldThrowWhenCreatingSuperAdmin() {
            CreateUserController controller = new CreateUserController(createUserUseCase);
            CreateUserRequest request = new CreateUserRequest("Root", "Senha@1234", UserRole.SUPER_ADMIN, UserStatus.ACTIVE);

            assertThatThrownBy(() -> controller.createUser(request))
                    .isInstanceOf(SuperAdminCreationNotAllowedException.class);

            verifyNoInteractions(createUserUseCase);
        }
    }

    @Nested
    @DisplayName("UpdateUserDataController")
    class UpdateUserDataControllerTests {
        @Mock
        private UpdateUserDataUseCase updateUserDataUseCase;

        @Test
        @DisplayName("Deve atualizar dados do usuário e retornar 204 No Content")
        void shouldUpdateUserData() {
            UpdateUserDataController controller = new UpdateUserDataController(updateUserDataUseCase);
            UUID userId = UUID.randomUUID();
            UpdateUserDataRequest request = new UpdateUserDataRequest("João Novo", UserRole.ADMIN, UserStatus.ACTIVE);

            ResponseEntity<Void> response = controller.updateUserData(userId, request);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
            verify(updateUserDataUseCase).execute(userId, request);
        }
    }

    @Nested
    @DisplayName("ChangePasswordController")
    class ChangePasswordControllerTests {
        @Mock
        private ChangePasswordUseCase changePasswordUseCase;

        @Test
        @DisplayName("Deve alterar senha do usuário e retornar 204 No Content")
        void shouldChangePassword() {
            ChangePasswordController controller = new ChangePasswordController(changePasswordUseCase);
            UUID userId = UUID.randomUUID();
            ChangePasswordRequest request = new ChangePasswordRequest("NovaSenha@1234");

            ResponseEntity<Void> response = controller.changePassword(userId, request);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
            verify(changePasswordUseCase).execute(userId, request);
        }
    }

    @Nested
    @DisplayName("DeleteUserController")
    class DeleteUserControllerTests {
        @Mock
        private DeleteUserUseCase deleteUserUseCase;

        @Test
        @DisplayName("Deve excluir usuário permanentemente e retornar 204 No Content")
        void shouldDeleteUser() {
            DeleteUserController controller = new DeleteUserController(deleteUserUseCase);
            UUID userId = UUID.randomUUID();

            ResponseEntity<Void> response = controller.deleteUser(userId);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
            verify(deleteUserUseCase).execute(userId);
        }
    }
}
