package garcias.api.identity.user.application.dto.requests;

public record ChangePasswordRequest(
        String newPassword,
        String accessKey,
        String cpf
) {
    public ChangePasswordRequest(String newPassword) {
        this(newPassword, null, null);
    }
}

