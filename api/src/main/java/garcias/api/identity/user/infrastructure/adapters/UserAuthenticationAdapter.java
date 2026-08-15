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

    public UserAuthenticationAdapter(UserRepository userRepository) {
        this.userRepository = userRepository;
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
}
