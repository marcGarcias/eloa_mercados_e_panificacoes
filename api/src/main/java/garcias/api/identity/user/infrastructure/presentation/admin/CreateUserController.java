package garcias.api.identity.user.infrastructure.presentation.admin;


import garcias.api.identity.user.application.dto.requests.CreateUserRequest;
import garcias.api.identity.user.application.usecases.CreateUserUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;


@RestController
@RequestMapping("/api/admin/users")
@Tag(
        name = "User Creation Management",
        description = """
                Administrative endpoints responsible for
                creating new users.
                
                User creation is restricted to authenticated
                SUPER_ADMIN users.
                """
)
public class CreateUserController {


    private final CreateUserUseCase createUserUseCase;


    public CreateUserController(
            CreateUserUseCase createUserUseCase
    ) {
        this.createUserUseCase = createUserUseCase;
    }


    @PostMapping
    @Operation(
            summary = "Create user",
            description = """
                    Creates a new user account.
                    
                    The user code must be unique.
                    
                    The password will be encrypted using Argon2
                    before being persisted.
                    
                    Only SUPER_ADMIN users can create accounts.
                    """,
            responses = {

                    @ApiResponse(
                            responseCode = "201",
                            description = "User created successfully"
                    ),

                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid user data"
                    ),

                    @ApiResponse(
                            responseCode = "403",
                            description = "Access denied"
                    ),

                    @ApiResponse(
                            responseCode = "409",
                            description = "User code already exists"
                    )
            }
    )
    public ResponseEntity<Void> createUser(

            @RequestBody
            CreateUserRequest request

    ) {


        createUserUseCase.execute(
                request
        );


        return ResponseEntity
                .created(
                        URI.create("/api/admin/users")
                )
                .build();
    }

}
