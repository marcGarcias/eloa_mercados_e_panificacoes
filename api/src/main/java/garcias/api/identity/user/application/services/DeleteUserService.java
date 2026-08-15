package garcias.api.identity.user.application.services;

import garcias.api.identity.user.application.dto.events.UserDeactivatedEvent;
import garcias.api.identity.user.application.usecases.DeleteUserUseCase;
import garcias.api.identity.user.domain.entities.User;
import garcias.api.identity.user.domain.exceptions.UserNotFoundException;
import garcias.api.identity.user.domain.repositories.UserRepository;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class DeleteUserService implements DeleteUserUseCase {


    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;


    public DeleteUserService(
            UserRepository userRepository,
            ApplicationEventPublisher eventPublisher
    ) {
        this.userRepository = userRepository;
        this.eventPublisher = eventPublisher;
    }


    @Override
    public void execute(UUID userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        eventPublisher.publishEvent(
                new UserDeactivatedEvent(user.getUserCode().value())
        );

        userRepository.delete(user);
    }
}
