package garcias.api.identity.user.infrastructure.persistence;

import garcias.api.identity.user.domain.entities.User;
import garcias.api.identity.user.domain.repositories.UserRepository;
import garcias.api.identity.user.domain.valueobjects.UserCode;
import garcias.api.identity.user.infrastructure.mapper.UserMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class UserRepositoryImpl
        implements UserRepository {

    private final SpringDataUserRepository repository;

    public UserRepositoryImpl(
            SpringDataUserRepository repository
    ) {
        this.repository = repository;
    }

    @Override
    public org.springframework.data.domain.Page<User> findAll(
            org.springframework.data.domain.Pageable pageable
    ) {
        return repository
                .findAll(pageable)
                .map(UserMapper::toDomain);
    }

    @Override
    public Optional<User> findById(
            UUID id
    ) {

        return repository
                .findById(id)
                .map(UserMapper::toDomain);
    }

    @Override
    public Long findNextUserCode() {

        return repository
                .findAll()
                .stream()
                .map(UserJpaEntity::getUserCode)
                .map(Long::valueOf)
                .max(Long::compare)
                .orElse(0L)
                + 1;
    }

    @Override
    public Optional<User> findByUserCode(
            UserCode userCode
    ) {

        return repository
                .findByUserCode(userCode.value())
                .map(UserMapper::toDomain);
    }

    @Override
    public boolean existsByCode(
            UserCode userCode
    ) {

        return repository.existsByUserCode(
                userCode.value()
        );
    }

    @Override
    public boolean existsAnyUser() {

        return repository.existsBy();
    }

    @Override
    public User save(
            User user
    ) {

        UserJpaEntity entity =
                UserMapper.toJpaEntity(user);

        UserJpaEntity saved =
                repository.save(entity);

        return UserMapper.toDomain(saved);
    }

    @Override
    public void delete(
            User user
    ) {

        repository.delete(
                UserMapper.toJpaEntity(user)
        );
    }
}