package garcias.api.identity.user.infrastructure.presentation.admin;

import garcias.api.identity.user.application.dto.requests.UpdateUserDataRequest;
import garcias.api.identity.user.application.usecases.UpdateUserDataUseCase;

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
        name = "User Data Management",
        description = """
                Administrative endpoints responsible for
                updating user information.
                
                These operations require SUPER_ADMIN permission.
                """
)
public class UpdateUserDataController {


    private final UpdateUserDataUseCase updateUserDataUseCase;


    public UpdateUserDataController(
            UpdateUserDataUseCase updateUserDataUseCase
    ) {
        this.updateUserDataUseCase = updateUserDataUseCase;
    }



    @PatchMapping("/{userId}")
    @Operation(
            summary = "Update user data",
            description = """
                    Updates editable user information.

                    Allowed fields:
                    - Name
                    - Role
                    - Status

                    This endpoint does not update credentials.
                    Password changes are handled by a dedicated endpoint.

                    Only authenticated SUPER_ADMIN users
                    are allowed to execute this operation.
                    """,

            responses = {

                    @ApiResponse(
                            responseCode = "204",
                            description = "User data updated successfully"
                    ),

                    @ApiResponse(
                            responseCode = "404",
                            description = "User not found"
                    ),

                    @ApiResponse(
                            responseCode = "403",
                            description = "User does not have permission"
                    ),

                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid request data"
                    )
            }
    )
    public ResponseEntity<Void> updateUserData(

            @Parameter(
                    name = "userId",
                    description = "Identifier of the user to update",
                    example = "550e8400-e29b-41d4-a716-446655440000"
            )
            @PathVariable UUID userId,


            @RequestBody
            UpdateUserDataRequest request

    ) {


        updateUserDataUseCase.execute(
                userId,
                request
        );


        return ResponseEntity.noContent()
                .build();
    }

}
