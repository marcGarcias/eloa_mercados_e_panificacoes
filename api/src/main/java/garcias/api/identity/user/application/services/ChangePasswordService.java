package garcias.api.identity.user.application.services;

import garcias.api.identity.user.application.dto.requests.ChangePasswordRequest;
import garcias.api.identity.user.application.usecases.ChangePasswordUseCase;
import garcias.api.identity.user.domain.entities.User;
import garcias.api.identity.user.domain.exceptions.InvalidUserPasswordException;
import garcias.api.identity.user.domain.exceptions.UserNotFoundException;
import garcias.api.identity.user.domain.repositories.UserRepository;
import garcias.api.identity.user.domain.valueobjects.Password;
import garcias.api.shared.security.application.PasswordHasher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;


@Service
@Transactional
public class ChangePasswordService
        implements ChangePasswordUseCase {


    private final UserRepository userRepository;

    private final PasswordHasher passwordHasher;


    public ChangePasswordService(
            UserRepository userRepository,
            PasswordHasher passwordHasher
    ) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
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
    }
}