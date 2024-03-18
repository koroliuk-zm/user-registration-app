package com.dkorolyuk.userregistrationapp.util;

import org.springframework.validation.BindingResult;

public class AppUtils {
    private AppUtils(){}

    public static String buildBindingErrorMessage(BindingResult bindingResult, String initialMessage) {
        StringBuilder errorMessage = new StringBuilder(initialMessage);
        bindingResult.getFieldErrors().forEach(error ->
                errorMessage.append(error.getDefaultMessage()).append("; "));
        return errorMessage.toString();
    }
}
