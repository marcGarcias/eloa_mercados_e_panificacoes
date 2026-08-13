package garcias.api.identity.user.domain.valueobjects;

public record Password(String value) {


    public Password(String value) {

        if(value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    "Password cannot be empty"
            );
        }

        this.value = value;
    }


    public static Password fromHash(String hash){

        return new Password(hash);
    }
}