package com.dkorolyuk.userregistrationapp.handler.impl;

import com.dkorolyuk.userregistrationapp.dto.UserDto;
import com.dkorolyuk.userregistrationapp.model.User;
import com.dkorolyuk.userregistrationapp.handler.UserHandler;
import org.springframework.stereotype.Component;

import static com.dkorolyuk.userregistrationapp.model.RegistrationStatus.PENDING;
import static com.dkorolyuk.userregistrationapp.util.Constants.VALIDATION_EMAIL_EXISTS_MESSAGE;
import static com.dkorolyuk.userregistrationapp.util.Constants.VALIDATION_PENDING_STATUS_MESSAGE;
import static com.dkorolyuk.userregistrationapp.util.Constants.VALIDATION_USER_EXISTS_MESSAGE;

@Component
public class UserHandlerImpl implements UserHandler {

    @Override
    public String buildDuplicationMessage(UserDto userDto, User existingUser) {
        StringBuilder builder = new StringBuilder();
        buildErrorMessage(existingUser.getRegistrationStatus(), PENDING, builder, VALIDATION_PENDING_STATUS_MESSAGE);
        buildErrorMessage(existingUser.getName(), userDto.name(), builder, VALIDATION_USER_EXISTS_MESSAGE);
        buildErrorMessage(existingUser.getEmail(), userDto.email(), builder, VALIDATION_EMAIL_EXISTS_MESSAGE);
        return builder.toString();
    }

    private void buildErrorMessage(Object o1, Object o2, StringBuilder builder, String errorMessage) {
        if (o1.equals(o2)) {
            builder.append(errorMessage).append("; ");
        }
    }
}

