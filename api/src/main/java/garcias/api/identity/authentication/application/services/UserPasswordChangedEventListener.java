package garcias.api.identity.authentication.application.services;

import garcias.api.identity.authentication.application.usecases.LogoutUseCase;
import garcias.api.identity.user.application.dto.events.UserPasswordChangedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class UserPasswordChangedEventListener {

    private final LogoutUseCase logoutUseCase;

    public UserPasswordChangedEventListener(LogoutUseCase logoutUseCase) {
        this.logoutUseCase = logoutUseCase;
    }

    @EventListener
    public void onUserPasswordChanged(UserPasswordChangedEvent event) {
        logoutUseCase.execute(event.userCode());
    }
}
