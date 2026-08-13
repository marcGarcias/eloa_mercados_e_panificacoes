package garcias.api.identity.user.infrastructure.mapper;

import garcias.api.identity.user.domain.entities.User;
import garcias.api.identity.user.domain.valueobjects.*;
import garcias.api.identity.user.infrastructure.persistence.UserJpaEntity;


public final class UserMapper {

    private UserMapper() {
    }

    public static UserJpaEntity toJpaEntity(User user) {

        UserJpaEntity entity = new UserJpaEntity();

        entity.setId(user.getId().value());
        entity.setName(user.getName().value());
        entity.setUserCode(user.getUserCode().value());
        entity.setPassword(user.getPassword().value());
        entity.setRole(user.getRole());
        entity.setStatus(user.getStatus());
        entity.setLastLoginAt(user.getLastLoginAt());
        entity.setCreatedAt(user.getCreatedAt());
        entity.setUpdatedAt(user.getUpdatedAt());

        return entity;
    }


    public static User toDomain(UserJpaEntity entity) {

        return User.restore(
                new UserId(entity.getId()),
                new UserName(entity.getName()),
                new UserCode(entity.getUserCode()),
                new Password(entity.getPassword()),
                entity.getRole(),
                entity.getStatus(),
                entity.getLastLoginAt(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}