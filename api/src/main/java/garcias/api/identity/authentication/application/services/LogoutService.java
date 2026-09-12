package garcias.api.identity.authentication.application.services;

import garcias.api.identity.authentication.application.security.RefreshTokenManager;
import garcias.api.identity.authentication.application.usecases.LogoutUseCase;
import garcias.api.identity.authentication.domain.repositories.RefreshTokenRepository;
import org.springframework.stereotype.Service;

@Service
public class LogoutService implements LogoutUseCase {

    private final RefreshTokenRepository refreshTokenRepository;
    private final RefreshTokenManager refreshTokenManager;

    public LogoutService(
            RefreshTokenRepository refreshTokenRepository,
            RefreshTokenManager refreshTokenManager
    ) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.refreshTokenManager = refreshTokenManager;
    }

    @Override
    public void execute(String userCode) {
        if (userCode != null && !userCode.isBlank()) {
            refreshTokenRepository.deleteByUserCode(userCode);
        }
    }

    @Override
    public void executeByToken(String refreshToken) {
        if (refreshToken != null && !refreshToken.isBlank()) {
            refreshTokenManager.revoke(refreshToken);
        }
    }
}

