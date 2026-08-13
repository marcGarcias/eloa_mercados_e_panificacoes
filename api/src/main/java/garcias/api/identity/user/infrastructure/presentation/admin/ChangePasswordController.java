package garcias.api.identity.user.infrastructure.presentation.admin;


import garcias.api.identity.user.application.dto.requests.ChangePasswordRequest;
import garcias.api.identity.user.application.usecases.ChangePasswordUseCase;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@RequestMapping("/api/admin/users")
@Tag(
        name = "User Password Management",
        description = """
                Administrative endpoints responsible for
                changing user passwords.

                Password changes are restricted to authenticated
                SUPER_ADMIN users.
                """
)
public class ChangePasswordController {


    private final ChangePasswordUseCase changePasswordUseCase;


    public ChangePasswordController(
            ChangePasswordUseCase changePasswordUseCase
    ) {
        this.changePasswordUseCase = changePasswordUseCase;
    }



    @PutMapping("/{userId}/password")
    @Operation(
            summary = "Change user password",
            description = """
                    Changes the password of an existing user.

                    This operation is only available for SUPER_ADMIN users.

                    The new password:
                    - Is validated against the current password
                    - Cannot be equal to the previous password
                    - Is encrypted using Argon2 before being stored

                    Users cannot change their own passwords.
                    """,
            responses = {

                    @ApiResponse(
                            responseCode = "204",
                            description = "Password changed successfully"
                    ),

                    @ApiResponse(
                            responseCode = "400",
                            description = """
                                    Invalid password.
                                    The new password may be equal
                                    to the current password.
                                    """
                    ),

                    @ApiResponse(
                            responseCode = "403",
                            description = """
                                    Authenticated user does not have
                                    SUPER_ADMIN permission.
                                    """
                    ),

                    @ApiResponse(
                            responseCode = "404",
                            description = "User not found"
                    )
            }
    )
    public ResponseEntity<Void> changePassword(

            @Parameter(
                    name = "userId",
                    description = "Identifier of the user whose password will be changed",
                    example = "550e8400-e29b-41d4-a716-446655440000"
            )
            @PathVariable UUID userId,


            @RequestBody
            ChangePasswordRequest request

    ) {


        changePasswordUseCase.execute(
                userId,
                request
        );


        return ResponseEntity.noContent()
                .build();
    }

}
