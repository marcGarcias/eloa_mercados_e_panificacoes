package garcias.api.identity.authentication.infrastructure.presentation.controller;

import garcias.api.identity.authentication.application.dto.requests.LoginRequest;
import garcias.api.identity.authentication.application.dto.responses.LoginResponse;
import garcias.api.identity.authentication.application.usecases.LoginUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(
        name = "Authentication",
        description = "Endpoints responsible for user authentication and token management."
)
public class AuthenticationController {

    private final LoginUseCase loginUseCase;

    public AuthenticationController(LoginUseCase loginUseCase) {
        this.loginUseCase = loginUseCase;
    }

    @Operation(
            summary = "Authenticate user",
            description = """
                    Authenticates a user using their user code and password.
                    
                    The user must exist, be active, and provide the correct
                    password. When authentication succeeds, the API returns
                    an access token and a refresh token.
                    
                    The access token must be sent in subsequent authenticated
                    requests using the Authorization header:
                    
                    Bearer {accessToken}
                    """
    )
    @ApiResponses({

            @ApiResponse(
                    responseCode = "200",
                    description = "User authenticated successfully.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = LoginResponse.class
                            ),
                            examples = @ExampleObject(
                                    name = "Successful authentication",
                                    value = """
                                            {
                                              "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
                                              "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
                                            }
                                            """
                            )
                    )
            ),

            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid credentials or inactive user.",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Invalid credentials",
                                    value = """
                                            {
                                              "message": "Invalid credentials."
                                            }
                                            """
                            )
                    )
            ),

            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data.",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Invalid request",
                                    value = """
                                            {
                                              "message": "Invalid request data."
                                            }
                                            """
                            )
                    )
            )
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {
        LoginResponse response = loginUseCase.execute(request);

        return ResponseEntity.ok(response);
    }
}
