package garcias.api.identity.user.domain.repositories;

import garcias.api.identity.user.domain.entities.User;
import garcias.api.identity.user.domain.valueobjects.UserCode;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

    Page<User> findAll(Pageable pageable);

    Optional<User> findById(UUID id);

    Long findNextUserCode();

    Optional<User> findByUserCode(
            UserCode userCode
    );

    boolean existsByCode(
            UserCode userCode
    );

    boolean existsAnyUser();

    boolean existsByRole(
            garcias.api.identity.user.domain.enums.UserRole role
    );

    User save(
            User user
    );

    void delete(
            User user
    );
}