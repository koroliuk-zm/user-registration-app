package com.dkorolyuk.userregistrationapp.handler;

import org.springframework.validation.BindingResult;

public interface GeneralHandler {
    String buildBindingErrorMessage(BindingResult bindingResult, String errorMessage);
}
