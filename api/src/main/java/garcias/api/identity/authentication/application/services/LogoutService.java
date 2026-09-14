package garcias.api.identity.authentication.application.services;

import garcias.api.identity.authentication.application.security.RefreshTokenManager;
import garcias.api.identity.authentication.application.usecases.LogoutUseCase;
import garcias.api.identity.authentication.domain.repositories.SessionRepository;
import org.springframework.stereotype.Service;

@Service
public class LogoutService implements LogoutUseCase {

    private final SessionRepository sessionRepository;
    private final RefreshTokenManager refreshTokenManager;

    public LogoutService(
            SessionRepository sessionRepository,
            RefreshTokenManager refreshTokenManager
    ) {
        this.sessionRepository = sessionRepository;
        this.refreshTokenManager = refreshTokenManager;
    }

    @Override
    public void execute(String userCode) {
        if (userCode != null && !userCode.isBlank()) {
            sessionRepository.revokeAllUserSessions(userCode);
        }
    }

    @Override
    public void executeByToken(String refreshToken) {
        if (refreshToken != null && !refreshToken.isBlank()) {
            refreshTokenManager.revoke(refreshToken);
        }
    }

    @Override
    public void executeBySessionId(String sessionId, String userCode) {
        if (sessionId != null && !sessionId.isBlank()) {
            sessionRepository.revokeSession(sessionId, userCode);
        }
    }
}

