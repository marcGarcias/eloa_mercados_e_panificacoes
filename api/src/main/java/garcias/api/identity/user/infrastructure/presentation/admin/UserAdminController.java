package garcias.api.identity.user.infrastructure.presentation.admin;

import garcias.api.identity.user.application.dto.responses.UserResponse;
import garcias.api.identity.user.application.usecases.ListUsersUseCase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/users")
public class UserAdminController {

    private final ListUsersUseCase listUsersUseCase;

    public UserAdminController(ListUsersUseCase listUsersUseCase) {
        this.listUsersUseCase = listUsersUseCase;
    }

    @GetMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Page<UserResponse>> listUsers(Pageable pageable) {
        Page<UserResponse> response = listUsersUseCase.execute(pageable);
        return ResponseEntity.ok(response);
    }
}
