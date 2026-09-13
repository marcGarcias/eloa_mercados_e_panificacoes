package garcias.api.identity.user.domain.entities;

import garcias.api.identity.user.domain.enums.UserRole;
import garcias.api.identity.user.domain.enums.UserStatus;
import garcias.api.identity.user.domain.valueobjects.Password;
import garcias.api.identity.user.domain.valueobjects.UserCode;
import garcias.api.identity.user.domain.valueobjects.UserId;
import garcias.api.identity.user.domain.valueobjects.UserName;
import garcias.api.shared.exceptions.ValueObjectCannotBeNullException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("User Domain Entity Unit Tests")
class UserTest {

    private final UserName userName = new UserName("Mariana Silva");
    private final UserCode userCode = new UserCode("0001");
    private final Password password = Password.fromHash("$argon2id$v=19$m=65536,t=3,p=1$samplehashsamplehash");

    @Test
    @DisplayName("Deve criar User através do factory method create com sucesso")
    void shouldCreateUserViaFactoryMethod() {
        User user = User.create(
                userName,
                userCode,
                password,
                UserRole.ADMIN,
                UserStatus.ACTIVE
        );

        assertThat(user.getId()).isNotNull();
        assertThat(user.getName()).isEqualTo(userName);
        assertThat(user.getUserCode()).isEqualTo(userCode);
        assertThat(user.getPassword()).isEqualTo(password);
        assertThat(user.getRole()).isEqualTo(UserRole.ADMIN);
        assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);
        assertThat(user.getCreatedAt()).isNotNull();
        assertThat(user.getUpdatedAt()).isNotNull();
        assertThat(user.getLastLoginAt()).isNull();
    }

    @Test
    @DisplayName("Deve restaurar User através do factory method restore com sucesso")
    void shouldRestoreUserSuccessfully() {
        UserId id = UserId.generate();
        LocalDateTime lastLogin = LocalDateTime.now().minusDays(1);
        LocalDateTime created = LocalDateTime.now().minusDays(10);
        LocalDateTime updated = LocalDateTime.now().minusHours(2);

        User user = User.restore(
                id,
                userName,
                userCode,
                password,
                UserRole.SUPER_ADMIN,
                UserStatus.ACTIVE,
                lastLogin,
                created,
                updated
        );

        assertThat(user.getId()).isEqualTo(id);
        assertThat(user.getName()).isEqualTo(userName);
        assertThat(user.getUserCode()).isEqualTo(userCode);
        assertThat(user.getRole()).isEqualTo(UserRole.SUPER_ADMIN);
        assertThat(user.getLastLoginAt()).isEqualTo(lastLogin);
        assertThat(user.getCreatedAt()).isEqualTo(created);
        assertThat(user.getUpdatedAt()).isEqualTo(updated);
    }

    @Test
    @DisplayName("Deve alterar nome e atualizar timestamp de atualização")
    void shouldChangeNameAndValidateNull() {
        User user = User.create(userName, userCode, password, UserRole.ADMIN, UserStatus.ACTIVE);
        UserName newName = new UserName("Mariana Souza");

        user.changeName(newName);
        assertThat(user.getName()).isEqualTo(newName);

        assertThatThrownBy(() -> user.changeName(null))
                .isInstanceOf(ValueObjectCannotBeNullException.class)
                .hasMessageContaining("User name");
    }

    @Test
    @DisplayName("Deve alterar senha e validar nulo")
    void shouldChangePasswordAndValidateNull() {
        User user = User.create(userName, userCode, password, UserRole.ADMIN, UserStatus.ACTIVE);
        Password newPassword = Password.fromHash("$argon2id$v=19$m=65536,t=3,p=1$anotherhash");

        user.changePassword(newPassword);
        assertThat(user.getPassword()).isEqualTo(newPassword);

        assertThatThrownBy(() -> user.changePassword(null))
                .isInstanceOf(ValueObjectCannotBeNullException.class)
                .hasMessageContaining("User password");
    }

    @Test
    @DisplayName("Deve alterar papel e validar nulo")
    void shouldChangeRoleAndValidateNull() {
        User user = User.create(userName, userCode, password, UserRole.ADMIN, UserStatus.ACTIVE);

        user.changeRole(UserRole.SUPER_ADMIN);
        assertThat(user.getRole()).isEqualTo(UserRole.SUPER_ADMIN);

        assertThatThrownBy(() -> user.changeRole(null))
                .isInstanceOf(ValueObjectCannotBeNullException.class)
                .hasMessageContaining("User role");
    }

    @Test
    @DisplayName("Deve alterar status e validar nulo")
    void shouldChangeStatusAndValidateNull() {
        User user = User.create(userName, userCode, password, UserRole.ADMIN, UserStatus.ACTIVE);

        user.changeStatus(UserStatus.INACTIVE);
        assertThat(user.getStatus()).isEqualTo(UserStatus.INACTIVE);

        user.changeStatus(UserStatus.ACTIVE);
        assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);

        user.deactivate();
        assertThat(user.getStatus()).isEqualTo(UserStatus.INACTIVE);

        user.activate();
        assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);

        assertThatThrownBy(() -> user.changeStatus(null))
                .isInstanceOf(ValueObjectCannotBeNullException.class)
                .hasMessageContaining("User status");
    }

    @Test
    @DisplayName("Deve registrar último login")
    void shouldRecordLogin() {
        User user = User.create(userName, userCode, password, UserRole.ADMIN, UserStatus.ACTIVE);
        assertThat(user.getLastLoginAt()).isNull();

        user.recordLogin();
        assertThat(user.getLastLoginAt()).isNotNull();
    }
}
