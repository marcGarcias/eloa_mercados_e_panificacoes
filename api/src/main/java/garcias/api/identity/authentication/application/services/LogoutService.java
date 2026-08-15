package garcias.api.identity.authentication.application.services;

import garcias.api.identity.authentication.application.usecases.LogoutUseCase;
import garcias.api.identity.authentication.domain.repositories.RefreshTokenRepository;
import org.springframework.stereotype.Service;

@Service
public class LogoutService implements LogoutUseCase {

    private final RefreshTokenRepository refreshTokenRepository;

    public LogoutService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Override
    public void execute(String userCode) {

        refreshTokenRepository.deleteByUserCode(userCode);
    }
}
