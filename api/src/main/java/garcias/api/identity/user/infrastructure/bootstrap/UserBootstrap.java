package garcias.api.identity.user.infrastructure.bootstrap;

import garcias.api.identity.user.domain.entities.User;
import garcias.api.identity.user.domain.enums.UserRole;
import garcias.api.identity.user.domain.enums.UserStatus;
import garcias.api.identity.user.domain.repositories.UserRepository;
import garcias.api.identity.user.domain.valueobjects.Password;
import garcias.api.identity.user.domain.valueobjects.UserCode;
import garcias.api.identity.user.domain.valueobjects.UserName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UserBootstrap implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(UserBootstrap.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserBootstrap(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (!userRepository.existsAnyUser()) {
            logger.info("Nenhum usuario encontrado no banco de dados. Criando administrador padrao...");

            String encodedPassword = passwordEncoder.encode("12345678");

            User admin = User.create(
                    new UserName("Administrador do Sistema"),
                    new UserCode("0001"),
                    new Password(encodedPassword),
                    UserRole.SUPER_ADMIN,
                    UserStatus.ACTIVE
            );

            userRepository.save(admin);
            logger.info("Usuario administrador criado com sucesso. Codigo de acesso: 0001");
        } else {
            logger.info("Ja existem usuarios no banco de dados. Pulando a criacao do admin padrao.");
        }
    }
}
