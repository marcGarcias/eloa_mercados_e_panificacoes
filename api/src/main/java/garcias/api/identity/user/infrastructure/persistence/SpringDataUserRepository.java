package garcias.api.identity.user.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SpringDataUserRepository
        extends JpaRepository<UserJpaEntity, UUID> {

    boolean existsBy();

    boolean existsByUserCode(
            String userCode
    );

    java.util.Optional<UserJpaEntity> findByUserCode(
            String userCode
    );
}