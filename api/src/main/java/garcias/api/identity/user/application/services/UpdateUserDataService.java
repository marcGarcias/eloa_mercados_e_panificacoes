package garcias.api.identity.user.application.services;

import garcias.api.identity.user.application.dto.requests.UpdateUserDataRequest;
import garcias.api.identity.user.application.usecases.UpdateUserDataUseCase;
import garcias.api.identity.user.domain.entities.User;
import garcias.api.identity.user.domain.repositories.UserRepository;
import garcias.api.identity.user.domain.valueobjects.UserName;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;


@Service
@Transactional
public class UpdateUserDataService implements UpdateUserDataUseCase {

    private final UserRepository userRepository;

    public UpdateUserDataService(
            UserRepository userRepository
    ) {
        this.userRepository = userRepository;
    }

    @Override
    public void execute(
            UUID userId,
            UpdateUserDataRequest request
    ) {

        User user = userRepository.findById(userId)
                .orElseThrow(
                        () -> new RuntimeException("User not found")
                );


        if (request.name() != null) {

            user.changeName(
                    new UserName(request.name())
            );
        }

        if (request.role() != null) {

            user.changeRole(
                    request.role()
            );
        }


        if (request.status() != null) {

            user.changeStatus(
                    request.status()
            );
        }


        userRepository.save(user);
    }
}