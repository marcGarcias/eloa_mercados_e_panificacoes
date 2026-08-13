package garcias.api.identity.user.infrastructure.services;


import garcias.api.identity.user.domain.repositories.UserRepository;
import garcias.api.identity.user.domain.services.UserCodeProvider;
import garcias.api.identity.user.domain.valueobjects.UserCode;

import org.springframework.stereotype.Component;


@Component
public class UserCodeGeneratorImpl
        implements UserCodeProvider {


    private final UserRepository userRepository;


    public UserCodeGeneratorImpl(
            UserRepository userRepository
    ) {
        this.userRepository = userRepository;
    }


    @Override
    public UserCode generate() {


        Long nextCode =
                userRepository.findNextUserCode();


        return UserCode.from(nextCode);

    }

}