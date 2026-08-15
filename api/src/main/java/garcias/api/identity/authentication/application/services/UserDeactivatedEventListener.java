package garcias.api.identity.authentication.application.services;

import garcias.api.identity.authentication.application.usecases.LogoutUseCase;
import garcias.api.identity.user.application.dto.events.UserDeactivatedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class UserDeactivatedEventListener {

    private final LogoutUseCase logoutUseCase;

    public UserDeactivatedEventListener(LogoutUseCase logoutUseCase) {
        this.logoutUseCase = logoutUseCase;
    }

    @EventListener
    public void onUserDeactivated(UserDeactivatedEvent event) {

        logoutUseCase.execute(event.userCode());
    }
}
