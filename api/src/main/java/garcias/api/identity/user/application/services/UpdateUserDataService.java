package garcias.api.identity.user.application.services;

import garcias.api.identity.user.application.dto.requests.UpdateUserDataRequest;
import garcias.api.identity.user.application.dto.events.UserDeactivatedEvent;
import garcias.api.identity.user.application.usecases.UpdateUserDataUseCase;
import garcias.api.identity.user.domain.entities.User;
import garcias.api.identity.user.domain.enums.UserStatus;
import garcias.api.identity.user.domain.exceptions.UserNotFoundException;
import garcias.api.identity.user.domain.repositories.UserRepository;
import garcias.api.identity.user.domain.valueobjects.UserName;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;


@Service
@Transactional
public class UpdateUserDataService implements UpdateUserDataUseCase {

    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    public UpdateUserDataService(
            UserRepository userRepository,
            ApplicationEventPublisher eventPublisher
    ) {
        this.userRepository = userRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void execute(
            UUID userId,
            UpdateUserDataRequest request
    ) {

        User user = userRepository.findById(userId)
                .orElseThrow(
                        UserNotFoundException::new
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

        if (request.status() == UserStatus.INACTIVE) {
            eventPublisher.publishEvent(
                    new UserDeactivatedEvent(user.getUserCode().value())
            );
        }
    }
}