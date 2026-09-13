package garcias.api.shared.exceptions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("GlobalExceptionHandler Business and General Errors Unit Tests")
class GlobalExceptionHandlerBusinessTest {

    private GlobalExceptionHandler handler;
    private MockHttpServletRequest request;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        request = new MockHttpServletRequest("GET", "/api/test");
    }

    @Test
    @DisplayName("Deve retornar 404 NOT_FOUND para NotFoundException")
    void shouldHandleNotFoundException() {
        NotFoundException ex = new ObjectNotFoundException(1L);
        ResponseEntity<ErrorResponse> response = handler.handleNotFoundException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(404);
        assertThat(response.getBody().message()).contains("Object with id 1 not found");
        assertThat(response.getBody().path()).isEqualTo("/api/test");
    }

    @Test
    @DisplayName("Deve retornar 409 CONFLICT para ConflictException")
    void shouldHandleConflictException() {
        ConflictException ex = new ObjectAlreadyExistsException("Category", "Padaria");
        ResponseEntity<ErrorResponse> response = handler.handleConflictException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(409);
        assertThat(response.getBody().message()).contains("Category already exists: Padaria");
    }

    @Test
    @DisplayName("Deve retornar 400 BAD_REQUEST para DomainException")
    void shouldHandleDomainException() {
        DomainException ex = new AttributeCannotBeEmptyException("Product name");
        ResponseEntity<ErrorResponse> response = handler.handleDomainException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(400);
        assertThat(response.getBody().message()).isEqualTo("Product name cannot be empty");
    }

    @Test
    @DisplayName("Deve retornar 400 BAD_REQUEST com campos formatados para BindException / MethodArgumentNotValidException")
    void shouldHandleValidationException() {
        Object target = new Object();
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(target, "request");
        bindingResult.addError(new FieldError("request", "name", "não deve estar em branco"));
        bindingResult.addError(new FieldError("request", "price", "deve ser maior que zero"));

        BindException bindException = new BindException(bindingResult);

        ResponseEntity<ErrorResponse> response = handler.handleValidationException(bindException, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(400);
        assertThat(response.getBody().message()).contains("name: não deve estar em branco");
        assertThat(response.getBody().message()).contains("price: deve ser maior que zero");
    }

    @Test
    @DisplayName("Deve retornar 413 CONTENT_TOO_LARGE para MaxUploadSizeExceededException")
    void shouldHandleMaxUploadSizeExceededException() {
        MaxUploadSizeExceededException ex = new MaxUploadSizeExceededException(10 * 1024 * 1024);
        ResponseEntity<ErrorResponse> response = handler.handleMaxUploadSizeExceededException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONTENT_TOO_LARGE);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(413);
        assertThat(response.getBody().message()).contains("10MB");
    }

    @Test
    @DisplayName("Deve retornar 500 INTERNAL_SERVER_ERROR para InternalServerException")
    void shouldHandleInternalServerException() {
        InternalServerException ex = new InternalServerException("Falha crítica no sistema");
        ResponseEntity<ErrorResponse> response = handler.handleInternalServerException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(500);
        assertThat(response.getBody().message()).isEqualTo("Falha crítica no sistema");
    }

    @Test
    @DisplayName("Deve retornar 400 BAD_REQUEST para RuntimeException não mapeada")
    void shouldHandleRuntimeException() {
        RuntimeException ex = new RuntimeException("Erro inesperado em tempo de execução");
        ResponseEntity<ErrorResponse> response = handler.handleRuntimeException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(400);
        assertThat(response.getBody().message()).isEqualTo("Ocorreu um erro ao processar a solicitação.");
    }
}
