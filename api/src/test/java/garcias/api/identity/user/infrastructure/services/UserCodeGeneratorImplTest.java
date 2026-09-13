package garcias.api.identity.user.infrastructure.services;

import garcias.api.identity.user.domain.repositories.UserRepository;
import garcias.api.identity.user.domain.valueobjects.UserCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários do UserCodeGeneratorImpl")
class UserCodeGeneratorImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserCodeGeneratorImpl userCodeGenerator;

    @Test
    @DisplayName("Deve gerar o próximo UserCode consultando o repositório")
    void shouldGenerateNextUserCode() {
        when(userRepository.findNextUserCode()).thenReturn(1005L);

        UserCode code = userCodeGenerator.generate();

        assertThat(code).isNotNull();
        assertThat(code.value()).isEqualTo("1005");
    }
}
