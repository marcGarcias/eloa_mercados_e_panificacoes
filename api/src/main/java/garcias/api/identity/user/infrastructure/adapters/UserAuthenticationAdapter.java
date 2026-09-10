package garcias.api.identity.user.infrastructure.adapters;

import garcias.api.identity.authentication.application.dto.results.UserAuthenticationDto;
import garcias.api.identity.authentication.application.ports.UserAuthenticationPort;
import garcias.api.identity.user.domain.repositories.UserRepository;
import garcias.api.identity.user.domain.valueobjects.UserCode;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserAuthenticationAdapter implements UserAuthenticationPort {

    private final UserRepository userRepository;
    private final garcias.api.identity.user.application.usecases.CreateUserUseCase createUserUseCase;

    public UserAuthenticationAdapter(UserRepository userRepository, garcias.api.identity.user.application.usecases.CreateUserUseCase createUserUseCase) {
        this.userRepository = userRepository;
        this.createUserUseCase = createUserUseCase;
    }

    @Override
    public Optional<UserAuthenticationDto> findByUserCode(String userCode) {
        return userRepository.findByUserCode(new UserCode(userCode))
                .map(user -> new UserAuthenticationDto(
                        user.getUserCode().value(),
                        user.getPassword().value(),
                        user.getRole().name(),
                        user.getStatus().name()
                ));
    }

    @Override
    public boolean existsAnyUser() {
        return userRepository.existsAnyUser();
    }

    @Override
    public String createInitialUser(String name, String password) {
        garcias.api.identity.user.application.dto.requests.CreateUserRequest request = 
            new garcias.api.identity.user.application.dto.requests.CreateUserRequest(
                name, password, garcias.api.identity.user.domain.enums.UserRole.SUPER_ADMIN, garcias.api.identity.user.domain.enums.UserStatus.ACTIVE);
        garcias.api.identity.user.domain.entities.User user = createUserUseCase.execute(request);
        return user.getUserCode().value();
    }
}
