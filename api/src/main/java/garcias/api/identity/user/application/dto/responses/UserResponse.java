package garcias.api.identity.user.application.dto.responses;

import java.time.LocalDateTime;

public record UserResponse(
        String id,
        String userCode,
        String name,
        String role,
        String status,
        LocalDateTime lastLoginAt
) {
    public static UserResponse from(garcias.api.identity.user.domain.entities.User user) {
        return new UserResponse(
                user.getId().value().toString(),
                user.getUserCode().value(),
                user.getName().value(),
                user.getRole().name(),
                user.getStatus().name(),
                user.getLastLoginAt()
        );
    }
}
