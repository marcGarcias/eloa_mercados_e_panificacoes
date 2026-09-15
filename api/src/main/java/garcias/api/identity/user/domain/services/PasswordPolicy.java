package garcias.api.identity.user.domain.services;

import garcias.api.identity.user.domain.exceptions.InvalidPasswordStrengthException;

import java.util.regex.Pattern;

public final class PasswordPolicy {

    private static final Pattern UPPERCASE_PATTERN = Pattern.compile("[A-Z]");
    private static final Pattern LOWERCASE_PATTERN = Pattern.compile("[a-z]");
    private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile("[^a-zA-Z0-9]");

    private PasswordPolicy() {
    }

    public static void validate(String rawPassword) {
        if (rawPassword == null || rawPassword.length() < 8) {
            throw new InvalidPasswordStrengthException();
        }

        if (!UPPERCASE_PATTERN.matcher(rawPassword).find()) {
            throw new InvalidPasswordStrengthException();
        }

        if (!LOWERCASE_PATTERN.matcher(rawPassword).find()) {
            throw new InvalidPasswordStrengthException();
        }

        if (!SPECIAL_CHAR_PATTERN.matcher(rawPassword).find()) {
            throw new InvalidPasswordStrengthException();
        }
    }
}
