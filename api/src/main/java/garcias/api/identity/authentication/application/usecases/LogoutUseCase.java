package garcias.api.identity.authentication.application.usecases;

public interface LogoutUseCase {

    void execute(String userCode);

    void executeByToken(String refreshToken);

    void executeBySessionId(String sessionId, String userCode);
}

