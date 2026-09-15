package garcias.api.identity.user.application.services;

import garcias.api.identity.authentication.domain.exceptions.InvalidSetupAccessKeyException;
import garcias.api.identity.authentication.domain.exceptions.InvalidSetupCpfException;
import garcias.api.identity.user.application.dto.requests.ChangePasswordRequest;
import garcias.api.identity.user.application.usecases.ChangePasswordUseCase;
import garcias.api.identity.user.domain.entities.User;
import garcias.api.identity.user.domain.enums.UserRole;
import garcias.api.identity.user.domain.exceptions.InvalidUserPasswordException;
import garcias.api.identity.user.domain.exceptions.UserNotFoundException;
import garcias.api.identity.user.domain.repositories.UserRepository;
import garcias.api.identity.user.domain.valueobjects.Password;
import garcias.api.shared.security.application.PasswordHasher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class ChangePasswordService
        implements ChangePasswordUseCase {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final org.springframework.context.ApplicationEventPublisher eventPublisher;

    @Value("${app.setup.access-key}")
    private String serverAccessKey;

    @Value("${app.setup.cpf}")
    private String serverCpf;

    public ChangePasswordService(
            UserRepository userRepository,
            PasswordHasher passwordHasher,
            org.springframework.context.ApplicationEventPublisher eventPublisher
    ) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void execute(
            UUID userId,
            ChangePasswordRequest request
    ) {

        User user = userRepository.findById(userId)
                .orElseThrow(
                        UserNotFoundException::new
                );

        if (user.getRole() == UserRole.SUPER_ADMIN) {
            if (request.cpf() == null || this.serverCpf == null
                    || !request.cpf().replaceAll("\\D", "").equals(this.serverCpf.replaceAll("\\D", ""))) {
                throw new InvalidSetupCpfException();
            }

            if (request.accessKey() == null || !request.accessKey().equals(this.serverAccessKey)) {
                throw new InvalidSetupAccessKeyException("Código de acesso incorreto.");
            }
        }

        garcias.api.identity.user.domain.services.PasswordPolicy.validate(request.newPassword());

        boolean samePassword =
                passwordHasher.matches(
                        request.newPassword(),
                        user.getPassword().value()
                );

        if (samePassword) {

            throw new InvalidUserPasswordException();
        }

        String encodedPassword =
                passwordHasher.hash(
                        request.newPassword()
                );

        user.changePassword(
                new Password(encodedPassword)
        );

        userRepository.save(user);

        eventPublisher.publishEvent(
                new garcias.api.identity.user.application.dto.events.UserPasswordChangedEvent(user.getUserCode().value())
        );
    }
}
