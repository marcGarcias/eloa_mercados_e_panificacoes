package garcias.api.identity.authentication.application.dto.events;

public record UserLoggedInEvent(
        String userCode
) {}
