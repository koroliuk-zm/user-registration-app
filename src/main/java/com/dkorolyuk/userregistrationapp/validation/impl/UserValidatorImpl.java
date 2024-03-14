package com.dkorolyuk.userregistrationapp.validation.impl;

import com.dkorolyuk.userregistrationapp.dto.UserDto;
import com.dkorolyuk.userregistrationapp.model.RegistrationStatus;
import com.dkorolyuk.userregistrationapp.model.User;
import com.dkorolyuk.userregistrationapp.validation.UserValidator;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;

import static com.dkorolyuk.userregistrationapp.util.Constants.VALIDATION_EMAIL_EXISTS_MESSAGE;
import static com.dkorolyuk.userregistrationapp.util.Constants.VALIDATION_PENDING_STATUS_MESSAGE;
import static com.dkorolyuk.userregistrationapp.util.Constants.VALIDATION_USER_EXISTS_MESSAGE;
import static java.util.Collections.unmodifiableSet;

@Component
public class UserValidatorImpl implements UserValidator {

    private final Set<BiFunction<UserDto, User, ResponseEntity<String>>> duplicationValidationStrategies;

    public UserValidatorImpl() {
        Set<BiFunction<UserDto, User, ResponseEntity<String>>> temp = new LinkedHashSet<>();
        temp.add(this::validatePendingStatus);
        temp.add(this::validateIsNameDuplicated);
        temp.add(this::validateIsEmailDuplicated);

        duplicationValidationStrategies = unmodifiableSet(temp);
    }

    @Override
    public ResponseEntity<String> validateUserDuplicates(UserDto userDto, User existingUser) {
        return duplicationValidationStrategies.stream()
                .map(strategy -> strategy.apply(userDto, existingUser))
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    private ResponseEntity<String> validatePendingStatus(UserDto userDto, User existingUser) {
        return existingUser.getRegistrationStatus() == RegistrationStatus.PENDING ?
                ResponseEntity.badRequest().body(VALIDATION_PENDING_STATUS_MESSAGE) :
                null;
    }

    private ResponseEntity<String> validateIsNameDuplicated(UserDto userDto, User existingUser) {
        return Objects.equals(existingUser.getName(), userDto.name()) ?
                ResponseEntity.badRequest().body(VALIDATION_USER_EXISTS_MESSAGE) :
                null;
    }

    private ResponseEntity<String> validateIsEmailDuplicated(UserDto userDto, User existingUser) {
        return Objects.equals(existingUser.getEmail(), userDto.email()) ?
                ResponseEntity.badRequest().body(VALIDATION_EMAIL_EXISTS_MESSAGE) :
                null;
    }
}

