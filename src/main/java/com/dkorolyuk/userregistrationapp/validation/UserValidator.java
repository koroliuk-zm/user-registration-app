package com.dkorolyuk.userregistrationapp.validation;

import com.dkorolyuk.userregistrationapp.dto.UserDto;
import com.dkorolyuk.userregistrationapp.model.User;
import org.springframework.http.ResponseEntity;

public interface UserValidator {

    ResponseEntity<String> validateUserDuplicates(UserDto userDto, User existingUser);
}
