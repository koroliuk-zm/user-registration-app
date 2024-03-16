package com.dkorolyuk.userregistrationapp.handler.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import static java.util.Collections.emptyList;
import static java.util.List.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GeneralHandlerImplTest {

    private GeneralHandlerImpl generalHandler;
    private BindingResult bindingResult;

    @BeforeEach
    void setUp() {
        generalHandler = new GeneralHandlerImpl();
        bindingResult = mock(BindingResult.class);
    }

    @Test
    void buildErrorResponse_errorsPresent() {
        FieldError error1 = new FieldError("user", "name", "name can't be null");
        FieldError error2 = new FieldError("user", "email", "email can't be null");

        when(bindingResult.getFieldErrors()).thenReturn(of(error1, error2));

        String message = generalHandler.buildBindingErrorMessage(bindingResult, "Initial message: ");

        assertThat(message).contains(error1.getDefaultMessage(), error2.getDefaultMessage());
    }

    @Test
    void buildErrorResponse_withoutErrors() {
        when(bindingResult.getFieldErrors()).thenReturn(emptyList());

        String response = generalHandler.buildBindingErrorMessage(bindingResult, "");

        assertThat(response).isBlank();
    }
}
