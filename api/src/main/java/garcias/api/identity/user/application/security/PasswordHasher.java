package garcias.api.identity.user.application.security;

public interface PasswordHasher {

    String hash(String rawPassword);

    boolean matches(
            String rawPassword,
            String hashedPassword
    );
}
