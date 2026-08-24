package garcias.api.identity.user.application.usecases;

import garcias.api.identity.user.application.dto.responses.UserResponse;
import garcias.api.identity.user.domain.repositories.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ListUsersUseCase {

    private final UserRepository userRepository;

    public ListUsersUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Page<UserResponse> execute(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(UserResponse::from);
    }
}
