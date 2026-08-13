package garcias.api.identity.authentication.application.security;

public interface AccessTokenProvider {

    String generate(String userCode, String role);
}
