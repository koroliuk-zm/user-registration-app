package com.dkorolyuk.userregistrationapp.controller;

import com.dkorolyuk.userregistrationapp.dto.UserDto;
import com.dkorolyuk.userregistrationapp.service.UserService;
import com.dkorolyuk.userregistrationapp.validation.UserValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

import static com.dkorolyuk.userregistrationapp.util.Constants.CONFIRM_REGISTRATION_MESSAGE;
import static com.dkorolyuk.userregistrationapp.util.Constants.INVALID_INPUT_MESSAGE;
import static com.dkorolyuk.userregistrationapp.util.Constants.SUCCESS_REGISTRATION_MESSAGE;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserValidator userValidator;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Validated @RequestBody UserDto user, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(buildErrorMessage(bindingResult));
        }

        return Optional.ofNullable(userService.getUser(user))
                .map(existingUser -> userValidator.validateUserDuplicates(user, existingUser))
                .orElseGet(() -> {
                    userService.saveUser(user);
                    userService.sendEmail(user.email());
                    return ResponseEntity.status(HttpStatus.CREATED).body(CONFIRM_REGISTRATION_MESSAGE);
                });
    }

    @PatchMapping("/confirm-registration")
    public ResponseEntity<String> confirmRegistration(@RequestParam String email) {
        userService.confirmRegistration(email);
        return ResponseEntity.ok(SUCCESS_REGISTRATION_MESSAGE + email);
    }

    private String buildErrorMessage(BindingResult bindingResult) {
        StringBuilder errorMessage = new StringBuilder(INVALID_INPUT_MESSAGE);
        for (FieldError error : bindingResult.getFieldErrors()) {
            errorMessage.append(error.getDefaultMessage()).append("; ");
        }
        return errorMessage.toString();
    }
}
