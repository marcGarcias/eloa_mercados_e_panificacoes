package garcias.api.identity.authentication.infrastructure.presentation.controller;

import garcias.api.identity.authentication.application.dto.requests.BootstrapUserRequest;
import garcias.api.identity.authentication.application.dto.responses.BootstrapUserResponse;
import garcias.api.identity.authentication.application.usecases.BootstrapUserUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(
        name = "Authentication",
        description = "Endpoints responsible for authentication and initial system setup."
)
public class BootstrapController {

    private final BootstrapUserUseCase bootstrapUserUseCase;

    public BootstrapController(
            BootstrapUserUseCase bootstrapUserUseCase
    ) {
        this.bootstrapUserUseCase =
                bootstrapUserUseCase;
    }

    @Operation(
            summary = "Create initial administrator",
            description = """
                    Creates the first user of the system.

                    This endpoint is only available while the system
                    does not contain any users.

                    The first user is automatically created with:

                    - Role: SUPER_ADMIN
                    - Status: ACTIVE

                    After the first user is created, this endpoint
                    can no longer be used.
                    """
    )
    @ApiResponses({

            @ApiResponse(
                    responseCode = "201",
                    description = "Initial administrator created successfully.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation =
                                            BootstrapUserResponse.class
                            ),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "id": "7d7c4d6a-7e1c-4e3c-9b2c-123456789abc",
                                              "name": "Administrador",
                                              "userCode": "0001",
                                              "role": "SUPER_ADMIN",
                                              "status": "ACTIVE"
                                            }
                                            """
                            )
                    )
            ),

            @ApiResponse(
                    responseCode = "409",
                    description = "Initial user has already been created.",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "message": "Initial user has already been created."
                                            }
                                            """
                            )
                    )
            ),

            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data."
            )
    })
    @PostMapping("/bootstrap")
    public ResponseEntity<BootstrapUserResponse> bootstrap(
            @Valid @RequestBody
            BootstrapUserRequest request
    ) {

        BootstrapUserResponse response =
                bootstrapUserUseCase.execute(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}