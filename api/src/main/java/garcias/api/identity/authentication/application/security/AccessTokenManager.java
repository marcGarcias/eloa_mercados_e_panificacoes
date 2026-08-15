package garcias.api.identity.authentication.application.security;

public interface AccessTokenManager {

    String generate(String userCode, String role, String status);

    String extractUserCode(String token);

    String extractRole(String token);

    String extractStatus(String token);

    boolean isValid(String token);


}
