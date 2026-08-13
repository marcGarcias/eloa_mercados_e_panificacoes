package garcias.api.identity.user.domain.valueobjects;

public record UserName(String value) {

    public UserName {

        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    "User name cannot be empty"
            );
        }

        value = value.trim();

        if (value.length() > 150) {
            throw new IllegalArgumentException(
                    "User name must have maximum 150 characters"
            );
        }
    }
}
