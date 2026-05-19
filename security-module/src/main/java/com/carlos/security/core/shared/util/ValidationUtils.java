package com.carlos.security.core.shared.util;

import java.util.UUID;
import java.util.regex.Pattern;

public class ValidationUtils {

    private ValidationUtils() {
    }

    //método estático que valide si un String es null o vacío
    public static boolean validateString(String string) {

        return string != null && !string.trim().isEmpty();

    }

    //método que valide formato de email usando regex
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    public static boolean validateEmail(String email) {

        if (email == null || email.isBlank()) return false;

        String normalized = email.trim().toLowerCase();

        return EMAIL_PATTERN.matcher(normalized).matches();
    }

    //método que valide si un UUID string es válido
    public static boolean isUUID(String cadena) {
        if (cadena == null || cadena.isEmpty()) {
            return false;
        }
        try {
            UUID.fromString(cadena);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    //método que valide si una página (pageNumber, pageSize) está dentro de rangos permitidos
    public static boolean isValidPageRequest(int pageNumber, int pageSize) {
        if (pageNumber < 0) {
            return false;
        }
        if (pageSize <= 0) {
            return false;
        }
        if (pageSize > 100) {
            return false;
        }
        return true;
    }

}
