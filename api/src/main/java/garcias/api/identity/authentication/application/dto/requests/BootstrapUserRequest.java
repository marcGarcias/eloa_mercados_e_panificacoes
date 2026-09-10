package garcias.api.identity.authentication.application.dto.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record BootstrapUserRequest(

        @NotBlank(message = "Name is required.")
        @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters.")
        String name,

        @NotBlank(message = "Password is required.")
        @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters.")
        String password,

        @NotBlank(message = "Access Key is required.")
        String accessKey,

        @NotBlank(message = "CPF is required.")
        @org.hibernate.validator.constraints.br.CPF(message = "CPF invalid.")
        String cpf

) {
}