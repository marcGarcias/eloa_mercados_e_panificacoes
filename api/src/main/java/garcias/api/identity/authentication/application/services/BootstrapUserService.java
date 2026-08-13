package garcias.api.identity.authentication.application.services;

import garcias.api.identity.authentication.application.dto.requests.BootstrapUserRequest;
import garcias.api.identity.authentication.application.dto.responses.BootstrapUserResponse;
import garcias.api.identity.authentication.application.usecases.BootstrapUserUseCase;
import garcias.api.identity.authentication.domain.exceptions.BootstrapAlreadyCompletedException;
import garcias.api.identity.user.application.dto.requests.CreateUserRequest;
import garcias.api.identity.user.application.usecases.CreateUserUseCase;
import garcias.api.identity.user.domain.entities.User;
import garcias.api.identity.user.domain.enums.UserRole;
import garcias.api.identity.user.domain.enums.UserStatus;
import garcias.api.identity.user.domain.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class BootstrapUserService
        implements BootstrapUserUseCase {

    private final UserRepository userRepository;
    private final CreateUserUseCase createUserUseCase;

    public BootstrapUserService(
            UserRepository userRepository,
            CreateUserUseCase createUserUseCase
    ) {
        this.userRepository = userRepository;
        this.createUserUseCase = createUserUseCase;
    }

    @Override
    public BootstrapUserResponse execute(
            BootstrapUserRequest request
    ) {

        if (userRepository.existsAnyUser()) {
            throw new BootstrapAlreadyCompletedException();
        }

        CreateUserRequest createUserRequest =
                new CreateUserRequest(
                        request.name(),
                        request.password(),
                        UserRole.SUPER_ADMIN,
                        UserStatus.ACTIVE
                );

        User user =
                createUserUseCase.execute(
                        createUserRequest
                );

        return new BootstrapUserResponse(
                user.getName().value(),
                user.getUserCode().value(),
                user.getRole(),
                user.getStatus()
        );
    }
}