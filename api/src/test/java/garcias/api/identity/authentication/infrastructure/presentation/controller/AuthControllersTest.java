package garcias.api.identity.authentication.infrastructure.presentation.controller;

import garcias.api.identity.authentication.application.dto.requests.BootstrapUserRequest;
import garcias.api.identity.authentication.application.dto.requests.LoginRequest;
import garcias.api.identity.authentication.application.dto.responses.BootstrapUserResponse;
import garcias.api.identity.authentication.application.dto.responses.LoginResponse;
import garcias.api.identity.authentication.application.dto.results.LoginResult;
import garcias.api.identity.authentication.application.usecases.BootstrapUserUseCase;
import garcias.api.identity.authentication.application.usecases.LoginUseCase;
import garcias.api.identity.authentication.application.usecases.LogoutUseCase;
import garcias.api.identity.authentication.application.usecases.RefreshTokenUseCase;
import garcias.api.identity.authentication.domain.exceptions.MissingRefreshTokenException;
import garcias.api.identity.authentication.infrastructure.security.csrf.CsrfOriginValidator;
import garcias.api.identity.authentication.infrastructure.security.jwt.JwtProperties;
import garcias.api.identity.user.application.dto.responses.UserResponse;
import garcias.api.identity.user.application.usecases.GetCurrentUserUseCase;
import garcias.api.identity.user.domain.enums.UserRole;
import garcias.api.identity.user.domain.enums.UserStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários dos Controllers de Autenticação")
class AuthControllersTest {

    @Nested
    @DisplayName("LoginController")
    class LoginControllerTests {
        @Mock
        private LoginUseCase loginUseCase;
        @Mock
        private JwtProperties jwtProperties;

        @Test
        @DisplayName("Deve autenticar com sucesso e definir cookie de refresh token")
        void shouldLoginSuccessfully() {
            LoginController controller = new LoginController(loginUseCase, jwtProperties);
            ReflectionTestUtils.setField(controller, "cookieSecure", false);
            ReflectionTestUtils.setField(controller, "cookieSameSite", "Lax");

            LoginRequest request = new LoginRequest("1001", "Senha@1234");
            LoginResult result = new LoginResult("access-token-xyz", "refresh-token-abc");

            when(loginUseCase.execute(request)).thenReturn(result);
            when(jwtProperties.getRefreshTokenExpiration()).thenReturn(604800L);

            MockHttpServletResponse response = new MockHttpServletResponse();
            ResponseEntity<LoginResponse> responseEntity = controller.login(request, response);

            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(responseEntity.getBody()).isNotNull();
            assertThat(responseEntity.getBody().accessToken()).isEqualTo("access-token-xyz");

            String setCookie = response.getHeader(HttpHeaders.SET_COOKIE);
            assertThat(setCookie).isNotNull();
            assertThat(setCookie).contains("refresh_token=refresh-token-abc");
            assertThat(setCookie).contains("Path=/api/auth");
            assertThat(setCookie).contains("HttpOnly");
        }
    }

    @Nested
    @DisplayName("LogoutController")
    class LogoutControllerTests {
        @Mock
        private LogoutUseCase logoutUseCase;
        @Mock
        private CsrfOriginValidator csrfOriginValidator;

        @Test
        @DisplayName("Deve efetuar logout invalidando o token no use case quando refresh_token presente")
        void shouldLogoutWithToken() {
            LogoutController controller = new LogoutController(logoutUseCase, csrfOriginValidator);
            ReflectionTestUtils.setField(controller, "cookieSecure", true);
            ReflectionTestUtils.setField(controller, "cookieSameSite", "None");

            MockHttpServletRequest request = new MockHttpServletRequest();
            MockHttpServletResponse response = new MockHttpServletResponse();

            ResponseEntity<Void> res = controller.logout("token-valido", request, response);

            assertThat(res.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
            verify(csrfOriginValidator).validate(request);
            verify(logoutUseCase).executeByToken("token-valido");

            String setCookie = response.getHeader(HttpHeaders.SET_COOKIE);
            assertThat(setCookie).isNotNull();
            assertThat(setCookie).contains("refresh_token=");
            assertThat(setCookie).contains("Max-Age=0");
        }

        @Test
        @DisplayName("Deve limpar cookie sem chamar use case quando refresh_token for nulo ou em branco")
        void shouldLogoutWithoutToken() {
            LogoutController controller = new LogoutController(logoutUseCase, csrfOriginValidator);
            ReflectionTestUtils.setField(controller, "cookieSecure", false);
            ReflectionTestUtils.setField(controller, "cookieSameSite", "Lax");

            MockHttpServletRequest request = new MockHttpServletRequest();
            MockHttpServletResponse response = new MockHttpServletResponse();

            ResponseEntity<Void> res = controller.logout("", request, response);

            assertThat(res.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
            verify(csrfOriginValidator).validate(request);
            verify(logoutUseCase, never()).executeByToken(any());
        }
    }

    @Nested
    @DisplayName("RefreshTokenController")
    class RefreshTokenControllerTests {
        @Mock
        private RefreshTokenUseCase refreshTokenUseCase;
        @Mock
        private JwtProperties jwtProperties;
        @Mock
        private CsrfOriginValidator csrfOriginValidator;

        @Test
        @DisplayName("Deve renovar token com sucesso quando refresh_token for fornecido")
        void shouldRefreshSuccessfully() {
            RefreshTokenController controller = new RefreshTokenController(refreshTokenUseCase, jwtProperties, csrfOriginValidator);
            ReflectionTestUtils.setField(controller, "cookieSecure", false);
            ReflectionTestUtils.setField(controller, "cookieSameSite", "Lax");

            MockHttpServletRequest request = new MockHttpServletRequest();
            MockHttpServletResponse response = new MockHttpServletResponse();

            LoginResult result = new LoginResult("new-access-token", "new-refresh-token");
            when(refreshTokenUseCase.execute("valid-old-token")).thenReturn(result);
            when(jwtProperties.getRefreshTokenExpiration()).thenReturn(604800L);

            ResponseEntity<LoginResponse> responseEntity = controller.refresh("valid-old-token", request, response);

            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(responseEntity.getBody().accessToken()).isEqualTo("new-access-token");
            verify(csrfOriginValidator).validate(request);

            String setCookie = response.getHeader(HttpHeaders.SET_COOKIE);
            assertThat(setCookie).isNotNull();
            assertThat(setCookie).contains("refresh_token=new-refresh-token");
        }

        @Test
        @DisplayName("Deve lançar MissingRefreshTokenException quando refresh_token for nulo")
        void shouldThrowWhenRefreshTokenNull() {
            RefreshTokenController controller = new RefreshTokenController(refreshTokenUseCase, jwtProperties, csrfOriginValidator);

            MockHttpServletRequest request = new MockHttpServletRequest();
            MockHttpServletResponse response = new MockHttpServletResponse();

            assertThatThrownBy(() -> controller.refresh(null, request, response))
                    .isInstanceOf(MissingRefreshTokenException.class);

            verify(csrfOriginValidator).validate(request);
            verifyNoInteractions(refreshTokenUseCase);
        }

        @Test
        @DisplayName("Deve lançar MissingRefreshTokenException quando refresh_token for em branco")
        void shouldThrowWhenRefreshTokenBlank() {
            RefreshTokenController controller = new RefreshTokenController(refreshTokenUseCase, jwtProperties, csrfOriginValidator);

            MockHttpServletRequest request = new MockHttpServletRequest();
            MockHttpServletResponse response = new MockHttpServletResponse();

            assertThatThrownBy(() -> controller.refresh("   ", request, response))
                    .isInstanceOf(MissingRefreshTokenException.class);
        }
    }

    @Nested
    @DisplayName("MeController")
    class MeControllerTests {
        @Mock
        private GetCurrentUserUseCase getCurrentUserUseCase;

        @Test
        @DisplayName("Deve retornar 401 se autenticação for nula ou principal nulo")
        void shouldReturn401WhenNotAuthenticated() {
            MeController controller = new MeController(getCurrentUserUseCase);

            assertThat(controller.me(null).getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);

            Authentication auth = mock(Authentication.class);
            when(auth.getPrincipal()).thenReturn(null);
            assertThat(controller.me(auth).getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        }

        @Test
        @DisplayName("Deve retornar dados do usuário quando autenticado")
        void shouldReturnUserDataWhenAuthenticated() {
            MeController controller = new MeController(getCurrentUserUseCase);
            Authentication auth = mock(Authentication.class);
            when(auth.getPrincipal()).thenReturn("1001");

            UserResponse userResponse = new UserResponse(
                    "550e8400-e29b-41d4-a716-446655440000",
                    "1001",
                    "Admin User",
                    "SUPER_ADMIN",
                    "ACTIVE",
                    null
            );
            when(getCurrentUserUseCase.execute("1001")).thenReturn(userResponse);

            ResponseEntity<UserResponse> res = controller.me(auth);

            assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(res.getBody()).isEqualTo(userResponse);
        }
    }

    @Nested
    @DisplayName("BootstrapController")
    class BootstrapControllerTests {
        @Mock
        private BootstrapUserUseCase bootstrapUserUseCase;

        @Test
        @DisplayName("Deve criar o primeiro usuário e retornar status 201 CREATED")
        void shouldBootstrapUserSuccessfully() {
            BootstrapController controller = new BootstrapController(bootstrapUserUseCase);

            BootstrapUserRequest request = new BootstrapUserRequest("Super Admin", "SenhaForte@1234", "master-key", "12345678901");
            BootstrapUserResponse response = new BootstrapUserResponse(
                    "Initial user created successfully.",
                    "1001"
            );

            when(bootstrapUserUseCase.execute(request)).thenReturn(response);

            ResponseEntity<BootstrapUserResponse> result = controller.bootstrap(request);

            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(result.getBody()).isEqualTo(response);
            verify(bootstrapUserUseCase).execute(request);
        }
    }
}
