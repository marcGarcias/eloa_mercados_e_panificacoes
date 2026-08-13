package garcias.api.identity.user.domain.repositories;

import garcias.api.identity.user.domain.entities.User;
import garcias.api.identity.user.domain.valueobjects.UserCode;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

    Optional<User> findById(UUID id);

    Long findNextUserCode();

    Optional<User> findByUserCode(
            UserCode userCode
    );

    boolean existsByCode(
            UserCode userCode
    );

    boolean existsAnyUser();

    User save(
            User user
    );

    void delete(
            User user
    );
}