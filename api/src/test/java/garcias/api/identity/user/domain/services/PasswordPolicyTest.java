package garcias.api.identity.user.domain.services;

import garcias.api.identity.user.domain.exceptions.InvalidPasswordStrengthException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("PasswordPolicy Domain Unit Tests")
class PasswordPolicyTest {

    @Test
    @DisplayName("Deve lançar exceção quando a senha for nula")
    void shouldThrowWhenPasswordIsNull() {
        assertThatThrownBy(() -> PasswordPolicy.validate(null))
                .isInstanceOf(InvalidPasswordStrengthException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "Ab1!", "1234567", "Abc!123"})
    @DisplayName("Deve lançar exceção quando a senha tiver menos de 8 caracteres")
    void shouldThrowWhenPasswordIsTooShort(String password) {
        assertThatThrownBy(() -> PasswordPolicy.validate(password))
                .isInstanceOf(InvalidPasswordStrengthException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"senhafraca123!", "tudoemminusculo@1", "12345678#abc"})
    @DisplayName("Deve lançar exceção quando faltar pelo menos uma letra maiúscula")
    void shouldThrowWhenPasswordLacksUppercase(String password) {
        assertThatThrownBy(() -> PasswordPolicy.validate(password))
                .isInstanceOf(InvalidPasswordStrengthException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"SENHAFORTE123!", "TUDOEMMAIUSCULO@1", "12345678#ABC"})
    @DisplayName("Deve lançar exceção quando faltar pelo menos uma letra minúscula")
    void shouldThrowWhenPasswordLacksLowercase(String password) {
        assertThatThrownBy(() -> PasswordPolicy.validate(password))
                .isInstanceOf(InvalidPasswordStrengthException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"SenhaForte1234", "MinhaSenha123", "SuperAdmin2026"})
    @DisplayName("Deve lançar exceção quando faltar pelo menos um caractere especial")
    void shouldThrowWhenPasswordLacksSpecialChar(String password) {
        assertThatThrownBy(() -> PasswordPolicy.validate(password))
                .isInstanceOf(InvalidPasswordStrengthException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "SenhaForte123!",
            "Admin@2026",
            "Abcdef1#",
            "P@ssw0rd",
            "M1nh@S3nh4",
            "Senha-Segura_2026",
            "Minha.Senha.123!"
    })
    @DisplayName("Deve aceitar com sucesso senhas válidas com 8+ caracteres, maiúscula, minúscula e caractere especial")
    void shouldAcceptValidPasswords(String validPassword) {
        assertThatCode(() -> PasswordPolicy.validate(validPassword))
                .doesNotThrowAnyException();
    }
}
