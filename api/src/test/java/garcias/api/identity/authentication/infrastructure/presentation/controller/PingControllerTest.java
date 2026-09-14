package garcias.api.identity.authentication.infrastructure.presentation.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("PingController Unit Tests")
class PingControllerTest {

    @Test
    @DisplayName("Should return 204 No Content on ping")
    void shouldReturnNoContent() {
        PingController controller = new PingController();
        ResponseEntity<Void> response = controller.ping();

        assertNotNull(response);
        assertEquals(204, response.getStatusCode().value());
    }
}
