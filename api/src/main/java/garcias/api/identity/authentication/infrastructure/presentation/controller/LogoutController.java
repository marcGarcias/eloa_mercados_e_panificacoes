package garcias.api.identity.authentication.infrastructure.presentation.controller;

import garcias.api.identity.authentication.application.usecases.LogoutUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(
        name = "Authentication - Logout",
        description = "Endpoint responsible for user logout."
)
public class LogoutController {

    private final LogoutUseCase logoutUseCase;

    public LogoutController(LogoutUseCase logoutUseCase) {
        this.logoutUseCase = logoutUseCase;
    }

    @Operation(
            summary = "Logout user",
            description = """
        Invalidates the user's refresh token and clears the cookie.
        
        This endpoint requires authentication. It reads the authenticated user's code
        and deletes all associated refresh tokens from the server, preventing new 
        access tokens from being generated. It also returns a Set-Cookie header to 
        clear the refresh token from the client's browser.
        """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Logged out successfully. Refresh token cookie is cleared."
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized. Invalid or missing access token.",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Unauthorized",
                                    value = """
                                            {
                                              "message": "Invalid credentials."
                                            }
                                            """
                            )
                    )
            )
    })
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @AuthenticationPrincipal String userCode,
            HttpServletResponse response
    ) {

        logoutUseCase.execute(userCode);

        ResponseCookie clearCookie = ResponseCookie
                .from("refresh_token", "")
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/api/auth")
                .maxAge(0) // 0 maxAge deletes the cookie
                .build();

        response.addHeader(
                HttpHeaders.SET_COOKIE,
                clearCookie.toString()
        );

        return ResponseEntity.noContent().build();
    }
}
