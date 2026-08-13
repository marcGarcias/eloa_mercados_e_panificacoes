package garcias.api.identity.user.application.dto.requests;

import garcias.api.identity.user.domain.enums.UserRole;
import garcias.api.identity.user.domain.enums.UserStatus;

public record CreateUserRequest(

        String name,

        String password,

        UserRole role,

        UserStatus status

) {}
