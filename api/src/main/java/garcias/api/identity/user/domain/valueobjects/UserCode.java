package garcias.api.identity.user.domain.valueobjects;

public record UserCode(String value) {


    public UserCode {

        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    "User code cannot be empty"
            );
        }


        if (!value.matches("\\d+")) {
            throw new IllegalArgumentException(
                    "User code must contain only numbers"
            );
        }


        if (value.length() < 4) {
            throw new IllegalArgumentException(
                    "User code must have at least 4 digits"
            );
        }
    }


    public static UserCode from(Long number) {

        String formatted = String.format("%04d", number);

        return new UserCode(formatted);
    }
}
