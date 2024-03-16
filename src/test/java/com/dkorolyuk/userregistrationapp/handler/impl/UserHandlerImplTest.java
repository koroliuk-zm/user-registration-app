package com.dkorolyuk.userregistrationapp.handler.impl;

import com.dkorolyuk.userregistrationapp.dto.UserDto;
import com.dkorolyuk.userregistrationapp.model.RegistrationStatus;
import com.dkorolyuk.userregistrationapp.model.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static com.dkorolyuk.userregistrationapp.util.Constants.VALIDATION_USER_EXISTS_MESSAGE;
import static org.assertj.core.api.Assertions.assertThat;

class UserHandlerImplTest {

    private final UserHandlerImpl userHandler = new UserHandlerImpl();

    @Test
    void buildResponse_badResponseWithAppropriateMessageWhenUserExists() {
        UserDto userDto = new UserDto("Dima", "dima@gmail.com", "password", "password");
        User existingUser = new User(1L, "Dima", "dimas@gmail.com", "password", RegistrationStatus.CONFIRMED, LocalDate.now());

        String response = userHandler.buildDuplicationMessage(userDto, existingUser);

        assertThat(response).contains(VALIDATION_USER_EXISTS_MESSAGE);
    }
}