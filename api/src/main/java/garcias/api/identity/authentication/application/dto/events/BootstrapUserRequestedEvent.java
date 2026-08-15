package garcias.api.identity.authentication.application.dto.events;

public record BootstrapUserRequestedEvent(
        String name,
        String password
) {}
