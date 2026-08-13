package garcias.api.identity.user.application.services;

import garcias.api.identity.user.application.usecases.DeleteUserUseCase;
import garcias.api.identity.user.domain.entities.User;
import garcias.api.identity.user.domain.repositories.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class DeleteUserService implements DeleteUserUseCase {


    private final UserRepository userRepository;


    public DeleteUserService(
            UserRepository userRepository
    ) {
        this.userRepository = userRepository;
    }


    @Override
    public void execute(UUID userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(
                        () -> new RuntimeException(
                                "User not found"
                        )
                );


        userRepository.delete(user);
    }
}
