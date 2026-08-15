package garcias.api.identity.authentication.application.services;

import garcias.api.identity.authentication.application.dto.events.BootstrapUserRequestedEvent;
import garcias.api.identity.authentication.application.dto.requests.BootstrapUserRequest;
import garcias.api.identity.authentication.application.dto.responses.BootstrapUserResponse;
import garcias.api.identity.authentication.application.ports.UserAuthenticationPort;
import garcias.api.identity.authentication.application.usecases.BootstrapUserUseCase;
import garcias.api.identity.authentication.domain.exceptions.BootstrapAlreadyCompletedException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class BootstrapUserService implements BootstrapUserUseCase {

    private final UserAuthenticationPort userAuthenticationPort;
    private final ApplicationEventPublisher eventPublisher;

    public BootstrapUserService(
            UserAuthenticationPort userAuthenticationPort,
            ApplicationEventPublisher eventPublisher
    ) {
        this.userAuthenticationPort = userAuthenticationPort;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public BootstrapUserResponse execute(BootstrapUserRequest request) {

        if (userAuthenticationPort.existsAnyUser()) {
            throw new BootstrapAlreadyCompletedException();
        }

        eventPublisher.publishEvent(
                new BootstrapUserRequestedEvent(
                        request.name(),
                        request.password()
                )
        );

        return new BootstrapUserResponse("Bootstrap process initiated successfully.");
    }
}