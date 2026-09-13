package garcias.api.identity.user.domain.valueobjects;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("UserId Value Object Unit Tests")
class UserIdTest {

    @Test
    @DisplayName("Deve criar UserId com UUID válido")
    void shouldCreateUserIdWithValidUUID() {
        UUID uuid = UUID.randomUUID();
        UserId userId = new UserId(uuid);

        assertThat(userId.value()).isEqualTo(uuid);
    }

    @Test
    @DisplayName("Deve gerar UserId aleatório com generate()")
    void shouldGenerateRandomUserId() {
        UserId userId = UserId.generate();

        assertThat(userId).isNotNull();
        assertThat(userId.value()).isNotNull();
    }

    @Test
    @DisplayName("Deve lançar NullPointerException quando UUID for nulo")
    void shouldThrowWhenUUIDIsNull() {
        assertThatThrownBy(() -> new UserId(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("User id cannot be null");
    }
}
