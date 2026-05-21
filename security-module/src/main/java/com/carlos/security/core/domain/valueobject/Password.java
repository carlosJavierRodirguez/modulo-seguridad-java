package com.carlos.security.core.domain.valueobject;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.regex.Pattern;

@Getter
@EqualsAndHashCode
public class Password {

    // Al menos 8 caracteres, una mayúscula, una minúscula, un número y un carácter especial
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#])[A-Za-z\\d@$!%*?&#]{8,}$"
    );

    private final String value;
    private final boolean isEncoded;

    private Password(String value, boolean isEncoded) {
        this.value = value;
        this.isEncoded = isEncoded;
    }

    public static Password ofPlainText(String plainPassword) {
        if (plainPassword == null || plainPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("La contraseña no puede ser nula ni estar vacía");
        }

        if (!PASSWORD_PATTERN.matcher(plainPassword).matches()) {
            throw new IllegalArgumentException(
                    "La contraseña debe tener al menos 8 caracteres y contener:" +
                            "mayúsculas, minúsculas, números y caracteres especiales"
            );
        }

        return new Password(plainPassword, false);
    }

    public static Password ofEncoded(String encodedPassword) {
        if (encodedPassword == null || encodedPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("La contraseña codificada no puede ser nula ni estar vacía");
        }

        return new Password(encodedPassword, true);
    }

    public void validate() {
        if (!isEncoded && !PASSWORD_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("La contraseña no cumple con los requisitos de seguridad");
        }
    }

    public int getStrength() {
        if (isEncoded) {
            return -1; // No se puede calcular para contraseñas ya codificadas
        }

        int strength = 0;

        if (value.length() >= 12) strength++;
        if (value.matches(".*[a-z].*")) strength++;
        if (value.matches(".*[A-Z].*")) strength++;
        if (value.matches(".*\\d.*")) strength++;
        if (value.matches(".*[@$!%*?&#].*")) strength++;

        return strength;
    }

    @Override
    public String toString() {
        return "********"; // Nunca mostrar la contraseña
    }

}
