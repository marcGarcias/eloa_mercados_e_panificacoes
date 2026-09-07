package garcias.api.identity.authentication.infrastructure.presentation.controller;

import garcias.api.identity.authentication.application.usecases.LogoutUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CookieValue;
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
    private final garcias.api.identity.authentication.infrastructure.security.csrf.CsrfOriginValidator csrfOriginValidator;

    @Value("${cookie.secure}")
    private boolean cookieSecure;

    @Value("${cookie.same-site}")
    private String cookieSameSite;

    public LogoutController(
            LogoutUseCase logoutUseCase,
            garcias.api.identity.authentication.infrastructure.security.csrf.CsrfOriginValidator csrfOriginValidator
    ) {
        this.logoutUseCase = logoutUseCase;
        this.csrfOriginValidator = csrfOriginValidator;
    }

    @Operation(
            summary = "Logout user",
            description = """
        Invalidates the user's refresh token session and clears the cookie.
        
        Reads the 'refresh_token' cookie and deletes its record from the server, 
        preventing new access tokens from being generated for this session. 
        Always returns a Set-Cookie header to clear the refresh token from the browser.
        """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Logged out successfully. Refresh token cookie is cleared."
            )
    })
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @CookieValue(value = "refresh_token", required = false) String refreshToken,
            jakarta.servlet.http.HttpServletRequest request,
            HttpServletResponse response
    ) {

        csrfOriginValidator.validate(request);

        if (refreshToken != null && !refreshToken.isBlank()) {
            logoutUseCase.executeByToken(refreshToken);
        }

        ResponseCookie clearCookie = ResponseCookie
                .from("refresh_token", "")
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite(cookieSameSite)
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
