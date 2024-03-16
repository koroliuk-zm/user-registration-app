package com.dkorolyuk.userregistrationapp.handler.impl;

import com.dkorolyuk.userregistrationapp.handler.GeneralHandler;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

@Component
public class GeneralHandlerImpl implements GeneralHandler {

    @Override
    public String buildBindingErrorMessage(BindingResult bindingResult, String initialMessage) {
        StringBuilder errorMessage = new StringBuilder(initialMessage);
        bindingResult.getFieldErrors().forEach(error ->
                errorMessage.append(error.getDefaultMessage()).append("; "));
        return errorMessage.toString();
    }
}
