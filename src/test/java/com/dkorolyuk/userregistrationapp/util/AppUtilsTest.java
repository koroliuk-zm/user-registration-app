package com.dkorolyuk.userregistrationapp.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import static com.dkorolyuk.userregistrationapp.util.AppUtils.buildBindingErrorMessage;
import static java.util.Collections.emptyList;
import static java.util.List.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppUtilsTest {

    @Mock
    private BindingResult bindingResult;

    @Test
    void buildErrorResponse_errorsPresent() {
        FieldError error1 = new FieldError("user", "name", "name can't be null");
        FieldError error2 = new FieldError("user", "email", "email can't be null");

        when(bindingResult.getFieldErrors()).thenReturn(of(error1, error2));

        String message = buildBindingErrorMessage(bindingResult, "Initial message: ");

        assertThat(message).contains(error1.getDefaultMessage(), error2.getDefaultMessage());
    }

    @Test
    void buildErrorResponse_withoutErrors() {
        when(bindingResult.getFieldErrors()).thenReturn(emptyList());

        String message = buildBindingErrorMessage(bindingResult, "");

        assertThat(message).isBlank();
    }
}
