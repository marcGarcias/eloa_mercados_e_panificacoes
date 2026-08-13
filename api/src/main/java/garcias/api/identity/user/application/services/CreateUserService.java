package garcias.api.identity.user.application.services;

import garcias.api.identity.user.application.dto.requests.CreateUserRequest;
import garcias.api.identity.user.application.security.PasswordHasher;
import garcias.api.identity.user.application.usecases.CreateUserUseCase;
import garcias.api.identity.user.domain.entities.User;
import garcias.api.identity.user.domain.repositories.UserRepository;
import garcias.api.identity.user.domain.services.UserCodeProvider;
import garcias.api.identity.user.domain.valueobjects.Password;
import garcias.api.identity.user.domain.valueobjects.UserCode;
import garcias.api.identity.user.domain.valueobjects.UserName;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CreateUserService
        implements CreateUserUseCase {


    private final UserRepository userRepository;

    private final UserCodeProvider userCodeProvider;

    private final PasswordHasher passwordHasher;


    public CreateUserService(
            UserRepository userRepository,
            UserCodeProvider userCodeProvider,
            PasswordHasher passwordHasher
    ){
        this.userRepository = userRepository;
        this.userCodeProvider = userCodeProvider;
        this.passwordHasher = passwordHasher;
    }


    @Override
    public User execute(
            CreateUserRequest request
    ) {


        UserCode code;

        do {

            code = userCodeProvider.generate();

        } while (
                userRepository.existsByCode(code)
        );



        Password password =
                Password.fromHash(
                        passwordHasher.hash(
                                request.password()
                        )
                );



        User user =
                User.create(
                        new UserName(request.name()),
                        code,
                        password,
                        request.role(),
                        request.status()
                );


        return userRepository.save(user);
    }
}
