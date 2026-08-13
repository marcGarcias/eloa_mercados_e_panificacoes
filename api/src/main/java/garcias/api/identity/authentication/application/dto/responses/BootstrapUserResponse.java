package garcias.api.identity.authentication.application.dto.responses;

import garcias.api.identity.user.domain.enums.UserRole;
import garcias.api.identity.user.domain.enums.UserStatus;


public record BootstrapUserResponse(

        String name,

        String userCode,

        UserRole role,

        UserStatus status

) {
}