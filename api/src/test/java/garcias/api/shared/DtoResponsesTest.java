package garcias.api.shared;

import garcias.api.catalog.category.application.dto.responses.CategoryAdmResponse;
import garcias.api.catalog.category.application.dto.responses.CategoryWebResponse;
import garcias.api.catalog.product.application.dto.responses.ProductAdminResponse;
import garcias.api.catalog.product.application.dto.responses.ProductPublicResponse;
import garcias.api.catalog.product.domain.enums.ProductStatus;
import garcias.api.identity.authentication.application.dto.responses.LoginResponse;
import garcias.api.identity.user.application.dto.responses.UserResponse;
import garcias.api.identity.user.domain.entities.User;
import garcias.api.identity.user.domain.enums.UserRole;
import garcias.api.identity.user.domain.enums.UserStatus;
import garcias.api.identity.user.domain.valueobjects.Password;
import garcias.api.identity.user.domain.valueobjects.UserCode;
import garcias.api.identity.user.domain.valueobjects.UserName;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Application DTO Responses Unit Tests")
class DtoResponsesTest {

    @Test
    @DisplayName("Deve instanciar e validar campos de todos os response DTOs")
    void shouldInstantiateAndValidateResponseDtos() {
        CategoryAdmResponse catAdm = new CategoryAdmResponse(1L, "Padaria");
        assertThat(catAdm.id()).isEqualTo(1L);
        assertThat(catAdm.name()).isEqualTo("Padaria");

        CategoryWebResponse catWeb = new CategoryWebResponse("Confeitaria");
        assertThat(catWeb.name()).isEqualTo("Confeitaria");

        ProductAdminResponse prodAdm = new ProductAdminResponse(
                10L, "Pão Francês", new BigDecimal("0.050"), 1L, "pao.webp", "Padaria", ProductStatus.ACTIVE
        );
        assertThat(prodAdm.id()).isEqualTo(10L);
        assertThat(prodAdm.name()).isEqualTo("Pão Francês");
        assertThat(prodAdm.weight()).isEqualByComparingTo("0.050");
        assertThat(prodAdm.position()).isEqualTo(1L);
        assertThat(prodAdm.photo()).isEqualTo("pao.webp");
        assertThat(prodAdm.categoryName()).isEqualTo("Padaria");
        assertThat(prodAdm.status()).isEqualTo(ProductStatus.ACTIVE);

        ProductPublicResponse prodPub = new ProductPublicResponse(
                "Bolo de Cenoura", new BigDecimal("0.800"), "bolo.webp", "Bolos", 2L
        );
        assertThat(prodPub.name()).isEqualTo("Bolo de Cenoura");
        assertThat(prodPub.weight()).isEqualByComparingTo("0.800");
        assertThat(prodPub.photoUrl()).isEqualTo("bolo.webp");
        assertThat(prodPub.categoryName()).isEqualTo("Bolos");
        assertThat(prodPub.position()).isEqualTo(2L);

        LoginResponse loginResp = new LoginResponse("mock-jwt-token");
        assertThat(loginResp.accessToken()).isEqualTo("mock-jwt-token");

        User user = User.create(
                new UserName("Maria"), new UserCode("0001"), Password.fromHash("hash"),
                UserRole.ADMIN, UserStatus.ACTIVE
        );
        UserResponse userResponse = UserResponse.from(user);
        assertThat(userResponse.name()).isEqualTo("Maria");
        assertThat(userResponse.userCode()).isEqualTo("0001");
        assertThat(userResponse.role()).isEqualTo("ADMIN");
        assertThat(userResponse.status()).isEqualTo("ACTIVE");
    }
}
