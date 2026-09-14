package garcias.api.identity.authentication.infrastructure.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth/ping")
@Tag(
        name = "Authentication - Ping",
        description = "Lightweight endpoint to verify active session status."
)
public class PingController {

    @Operation(
            summary = "Ping active session",
            description = """
        Verifies if the client has a valid and active session in Redis.
        Protected by JwtAuthenticationFilter. Returns 204 No Content if session is active.
        """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Session is active and valid."
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Session is invalid, revoked or expired."
            )
    })
    @GetMapping
    public ResponseEntity<Void> ping() {
        return ResponseEntity.noContent().build();
    }
}
