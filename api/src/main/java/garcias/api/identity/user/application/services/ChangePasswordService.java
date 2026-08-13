package garcias.api.identity.user.application.services;

import garcias.api.identity.user.application.dto.requests.ChangePasswordRequest;

import garcias.api.identity.user.application.usecases.ChangePasswordUseCase;
import garcias.api.identity.user.domain.entities.User;
import garcias.api.identity.user.domain.repositories.UserRepository;
import garcias.api.identity.user.domain.valueobjects.Password;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;


@Service
@Transactional
public class ChangePasswordService
        implements ChangePasswordUseCase {


    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;


    public ChangePasswordService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public void execute(
            UUID userId,
            ChangePasswordRequest request
    ) {

        User user = userRepository.findById(userId)
                .orElseThrow(
                        () -> new RuntimeException(
                                "User not found"
                        )
                );


        boolean samePassword =
                passwordEncoder.matches(
                        request.newPassword(),
                        user.getPassword().value()
                );


        if (samePassword) {

            throw new IllegalArgumentException(
                    "New password cannot be the same as current password"
            );
        }


        String encodedPassword =
                passwordEncoder.encode(
                        request.newPassword()
                );


        user.changePassword(
                new Password(encodedPassword)
        );


        userRepository.save(user);
    }
}