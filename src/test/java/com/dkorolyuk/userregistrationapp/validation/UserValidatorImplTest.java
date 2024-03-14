package com.dkorolyuk.userregistrationapp.validation;

import com.dkorolyuk.userregistrationapp.dto.UserDto;
import com.dkorolyuk.userregistrationapp.model.RegistrationStatus;
import com.dkorolyuk.userregistrationapp.model.User;
import com.dkorolyuk.userregistrationapp.validation.impl.UserValidatorImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

import static com.dkorolyuk.userregistrationapp.util.Constants.VALIDATION_EMAIL_EXISTS_MESSAGE;
import static com.dkorolyuk.userregistrationapp.util.Constants.VALIDATION_PENDING_STATUS_MESSAGE;
import static com.dkorolyuk.userregistrationapp.util.Constants.VALIDATION_USER_EXISTS_MESSAGE;
import static org.assertj.core.api.Assertions.assertThat;

class UserValidatorImplTest {

    private UserValidatorImpl userValidator;

    @BeforeEach
    void setUp() {
        userValidator = new UserValidatorImpl();
    }

    @Test
    void validateUserDuplicates_isBadRequestWhenUserHasPendingStatus() {
        UserDto userDto = new UserDto("Ivan", "ivan@gmail.com", "password123", "password123");
        User existingUser = new User(1L, "Ivan", "ivan@gmail.com", "password456", RegistrationStatus.PENDING, LocalDate.now());

        ResponseEntity<String> response = userValidator.validateUserDuplicates(userDto, existingUser);

        assertThat(response.getBody()).isEqualTo(VALIDATION_PENDING_STATUS_MESSAGE);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void validateUserDuplicates_isBadRequestWhenNameIsDuplicate() {
        UserDto userDto = new UserDto("Peter", "peter@gmail.com", "password123", "password123");
        User existingUser = new User(1L, "Peter", "peter@gmail.com", "password456", RegistrationStatus.CONFIRMED, LocalDate.now());

        ResponseEntity<String> response = userValidator.validateUserDuplicates(userDto, existingUser);

        assertThat(response.getBody()).isEqualTo(VALIDATION_USER_EXISTS_MESSAGE);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void validateUserDuplicates_isBadRequestWhenEmailIsDuplicate() {
        UserDto userDto = new UserDto("Alex", "alex@gmail.com", "password123", "password123");
        User existingUser = new User(1L, "Michael", "alex@gmail.com", "password456", RegistrationStatus.CONFIRMED, LocalDate.now());

        ResponseEntity<String> response = userValidator.validateUserDuplicates(userDto, existingUser);

        assertThat(response.getBody()).isEqualTo(VALIDATION_EMAIL_EXISTS_MESSAGE);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}