package garcias.api.identity.user.application.services;

import garcias.api.identity.authentication.application.dto.events.UserLoggedInEvent;
import garcias.api.identity.user.domain.entities.User;
import garcias.api.identity.user.domain.repositories.UserRepository;
import garcias.api.identity.user.domain.valueobjects.UserCode;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserLoggedInEventListener {

    private final UserRepository userRepository;

    public UserLoggedInEventListener(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @EventListener
    @Async
    public void handle(UserLoggedInEvent event) {
        Optional<User> userOpt = userRepository.findByUserCode(new UserCode(event.userCode()));
        userOpt.ifPresent(user -> {
            user.recordLogin();
            userRepository.save(user);
        });
    }
}
