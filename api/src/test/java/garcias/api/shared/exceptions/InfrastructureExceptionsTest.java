package garcias.api.shared.exceptions;

import garcias.api.catalog.category.infrastructure.exceptions.InvalidCategoryEntityStateException;
import garcias.api.catalog.product.infrastructure.exceptions.ImageStorageException;
import garcias.api.catalog.product.infrastructure.exceptions.InvalidProductEntityStateException;
import garcias.api.identity.authentication.infrastructure.security.exceptions.TokenGenerationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Infrastructure Exceptions Unit Tests")
class InfrastructureExceptionsTest {

    @Test
    @DisplayName("Deve instanciar InvalidCategoryEntityStateException")
    void shouldInstantiateInvalidCategoryEntityStateException() {
        InvalidCategoryEntityStateException ex = new InvalidCategoryEntityStateException("Estado inválido da categoria");
        assertThat(ex.getMessage()).isEqualTo("Estado inválido da categoria");
    }

    @Test
    @DisplayName("Deve instanciar InvalidProductEntityStateException")
    void shouldInstantiateInvalidProductEntityStateException() {
        InvalidProductEntityStateException ex = new InvalidProductEntityStateException("Estado inválido do produto");
        assertThat(ex.getMessage()).isEqualTo("Estado inválido do produto");
    }

    @Test
    @DisplayName("Deve instanciar ImageStorageException com causa")
    void shouldInstantiateImageStorageException() {
        RuntimeException cause = new RuntimeException("Disco cheio");
        ImageStorageException ex = new ImageStorageException("Erro ao gravar imagem", cause);
        assertThat(ex.getMessage()).isEqualTo("Erro ao gravar imagem");
        assertThat(ex.getCause()).isEqualTo(cause);
    }

    @Test
    @DisplayName("Deve instanciar TokenGenerationException com causa")
    void shouldInstantiateTokenGenerationException() {
        RuntimeException cause = new RuntimeException("Chave privada inválida");
        TokenGenerationException ex = new TokenGenerationException("Erro ao gerar token", cause);
        assertThat(ex.getMessage()).isEqualTo("Erro ao gerar token");
        assertThat(ex.getCause()).isEqualTo(cause);
    }

    @Test
    @DisplayName("Deve instanciar InternalServerException com mensagem e com causa")
    void shouldInstantiateInternalServerException() {
        InternalServerException ex1 = new InternalServerException("Erro interno");
        assertThat(ex1.getMessage()).isEqualTo("Erro interno");

        RuntimeException cause = new RuntimeException("Falha de IO");
        InternalServerException ex2 = new InternalServerException("Mensagem customizada", cause);
        assertThat(ex2.getMessage()).isEqualTo("Mensagem customizada");
        assertThat(ex2.getCause()).isEqualTo(cause);
    }
}
