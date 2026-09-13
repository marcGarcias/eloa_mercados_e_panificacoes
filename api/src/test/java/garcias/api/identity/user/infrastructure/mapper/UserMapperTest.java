package garcias.api.identity.user.infrastructure.mapper;

import garcias.api.identity.user.domain.entities.User;
import garcias.api.identity.user.domain.enums.UserRole;
import garcias.api.identity.user.domain.enums.UserStatus;
import garcias.api.identity.user.domain.valueobjects.Password;
import garcias.api.identity.user.domain.valueobjects.UserCode;
import garcias.api.identity.user.domain.valueobjects.UserName;
import garcias.api.identity.user.infrastructure.persistence.UserJpaEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("UserMapper and UserJpaEntity Unit Tests")
class UserMapperTest {

    @Test
    @DisplayName("Deve mapear Domain User para UserJpaEntity com toJpaEntity()")
    void shouldMapDomainUserToJpaEntity() {
        User user = User.create(
                new UserName("Carlos Eduardo"),
                new UserCode("0001"),
                Password.fromHash("hashed_pwd"),
                UserRole.ADMIN,
                UserStatus.ACTIVE
        );

        UserJpaEntity entity = UserMapper.toJpaEntity(user);

        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(user.getId().value());
        assertThat(entity.getName()).isEqualTo("Carlos Eduardo");
        assertThat(entity.getUserCode()).isEqualTo("0001");
        assertThat(entity.getPassword()).isEqualTo("hashed_pwd");
        assertThat(entity.getRole()).isEqualTo(UserRole.ADMIN);
        assertThat(entity.getStatus()).isEqualTo(UserStatus.ACTIVE);
        assertThat(entity.getCreatedAt()).isNotNull();
        assertThat(entity.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Deve mapear UserJpaEntity para Domain User com toDomain()")
    void shouldMapJpaEntityToDomainUser() {
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        UserJpaEntity entity = new UserJpaEntity();
        entity.setId(id);
        entity.setName("Fernanda Lima");
        entity.setUserCode("0002");
        entity.setPassword("hash");
        entity.setRole(UserRole.SUPER_ADMIN);
        entity.setStatus(UserStatus.ACTIVE);
        entity.setLastLoginAt(now.minusHours(1));
        entity.setCreatedAt(now.minusDays(5));
        entity.setUpdatedAt(now);

        User domain = UserMapper.toDomain(entity);

        assertThat(domain).isNotNull();
        assertThat(domain.getId().value()).isEqualTo(id);
        assertThat(domain.getName().value()).isEqualTo("Fernanda Lima");
        assertThat(domain.getUserCode().value()).isEqualTo("0002");
        assertThat(domain.getRole()).isEqualTo(UserRole.SUPER_ADMIN);
        assertThat(domain.getStatus()).isEqualTo(UserStatus.ACTIVE);
        assertThat(domain.getLastLoginAt()).isEqualTo(now.minusHours(1));
    }
}
