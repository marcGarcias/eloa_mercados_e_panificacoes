package garcias.api.identity.user.infrastructure.presentation.admin;


import garcias.api.identity.user.application.usecases.DeleteUserUseCase;

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
        name = "User Deletion Management",
        description = """
                Administrative endpoints responsible for
                permanently removing users from the system.

                These operations require SUPER_ADMIN permission.
                """
)
public class DeleteUserController {


    private final DeleteUserUseCase deleteUserUseCase;


    public DeleteUserController(
            DeleteUserUseCase deleteUserUseCase
    ) {
        this.deleteUserUseCase = deleteUserUseCase;
    }



    @DeleteMapping("/{userId}")
    @Operation(
            summary = "Delete user permanently",
            description = """
                    Permanently removes a user from the database.

                    This operation cannot be undone.

                    Only authenticated SUPER_ADMIN users
                    are allowed to perform this action.

                    Before deletion, the system verifies
                    if the user exists.
                    """,
            responses = {

                    @ApiResponse(
                            responseCode = "204",
                            description = "User deleted successfully"
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
    public ResponseEntity<Void> deleteUser(

            @Parameter(
                    name = "userId",
                    description = "Identifier of the user to delete",
                    example = "550e8400-e29b-41d4-a716-446655440000"
            )
            @PathVariable UUID userId

    ) {


        deleteUserUseCase.execute(
                userId
        );


        return ResponseEntity.noContent()
                .build();
    }

}