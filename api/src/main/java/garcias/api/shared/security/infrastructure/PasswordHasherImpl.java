package garcias.api.shared.security.infrastructure;

import garcias.api.shared.security.application.PasswordHasher;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Component;


@Component
public class PasswordHasherImpl
        implements PasswordHasher {


    private final Argon2PasswordEncoder encoder;


    public PasswordHasherImpl() {

        this.encoder =
                Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();
    }

    @Override
    public String hash(String rawPassword) {

        return encoder.encode(rawPassword);
    }


    @Override
    public boolean matches(
            String rawPassword,
            String hashedPassword
    ) {

        return encoder.matches(
                rawPassword,
                hashedPassword
        );
    }
}