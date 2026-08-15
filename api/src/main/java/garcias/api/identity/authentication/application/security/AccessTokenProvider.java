package garcias.api.identity.authentication.application.security;

public interface AccessTokenProvider {

    String generate(String userCode, String role);

    String extractUserCode(String token);

    String extractRole(String token);

    boolean isValid(String token);


}
