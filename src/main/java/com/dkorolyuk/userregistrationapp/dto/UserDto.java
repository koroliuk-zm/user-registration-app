package com.dkorolyuk.userregistrationapp.dto;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import static com.dkorolyuk.userregistrationapp.util.Constants.EMAIL_REGEXP;
import static com.dkorolyuk.userregistrationapp.util.Constants.NAME_REGEXP;
import static com.dkorolyuk.userregistrationapp.util.Constants.PASSWORD_REGEXP;

public record UserDto(
        @NotNull(message = "{user.name.null}")
        @Size(min = 2, max = 20, message = "{user.name.size}")
        @Pattern(regexp = NAME_REGEXP, message = "{user.name.pattern}")
        String name,

        @NotNull(message = "{user.email.null}")
        @Pattern(regexp = EMAIL_REGEXP, message = "{user.email.pattern}")
        String email,

        @NotNull(message = "{user.password.null}")
        @Size(min = 8, max = 16, message = "[user.password.size}")
        @Pattern(regexp = PASSWORD_REGEXP, message = "{user.password.pattern}")
        String password
) {
}
