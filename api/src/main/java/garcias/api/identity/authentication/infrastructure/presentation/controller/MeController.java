package garcias.api.identity.authentication.infrastructure.presentation.controller;

import garcias.api.identity.user.application.dto.responses.UserResponse;
import garcias.api.identity.user.application.usecases.GetCurrentUserUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth/me")
public class MeController {

    private final GetCurrentUserUseCase getCurrentUserUseCase;

    public MeController(GetCurrentUserUseCase getCurrentUserUseCase) {
        this.getCurrentUserUseCase = getCurrentUserUseCase;
    }

    @GetMapping
    public ResponseEntity<UserResponse> me(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            return ResponseEntity.status(401).build();
        }

        String userCode = authentication.getPrincipal().toString();
        UserResponse response = getCurrentUserUseCase.execute(userCode);
        
        return ResponseEntity.ok(response);
    }
}
