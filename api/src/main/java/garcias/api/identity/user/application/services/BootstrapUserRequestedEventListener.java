package garcias.api.identity.user.application.services;

import garcias.api.identity.authentication.application.dto.events.BootstrapUserRequestedEvent;
import garcias.api.identity.user.application.dto.requests.CreateUserRequest;
import garcias.api.identity.user.application.usecases.CreateUserUseCase;
import garcias.api.identity.user.domain.enums.UserRole;
import garcias.api.identity.user.domain.enums.UserStatus;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class BootstrapUserRequestedEventListener {

    private final CreateUserUseCase createUserUseCase;

    public BootstrapUserRequestedEventListener(CreateUserUseCase createUserUseCase) {
        this.createUserUseCase = createUserUseCase;
    }

    @EventListener
    @Async
    public void handle(BootstrapUserRequestedEvent event) {
        CreateUserRequest request = new CreateUserRequest(
                event.name(),
                event.password(),
                UserRole.SUPER_ADMIN,
                UserStatus.ACTIVE
        );

        createUserUseCase.execute(request);
    }
}
