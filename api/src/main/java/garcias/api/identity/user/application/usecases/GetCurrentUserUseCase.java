package garcias.api.identity.user.application.usecases;

import garcias.api.identity.user.application.dto.responses.UserResponse;
import garcias.api.identity.user.domain.entities.User;
import garcias.api.identity.user.domain.repositories.UserRepository;
import garcias.api.identity.user.domain.valueobjects.UserCode;
import garcias.api.identity.user.domain.exceptions.UserNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class GetCurrentUserUseCase {

    private final UserRepository userRepository;

    public GetCurrentUserUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponse execute(String userCode) {
        User user = userRepository.findByUserCode(new UserCode(userCode))
                .orElseThrow(() -> new UserNotFoundException());

        return UserResponse.from(user);
    }
}
