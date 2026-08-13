package garcias.api.identity.user.application.dto.requests;

import garcias.api.identity.user.domain.enums.UserRole;
import garcias.api.identity.user.domain.enums.UserStatus;

public record UpdateUserDataRequest(

        String name,
        UserRole role,
        UserStatus status

) {
}
