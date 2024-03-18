package com.dkorolyuk.userregistrationapp.controller;

import com.dkorolyuk.userregistrationapp.dto.UserDto;
import com.dkorolyuk.userregistrationapp.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

import static com.dkorolyuk.userregistrationapp.util.AppUtils.buildBindingErrorMessage;
import static com.dkorolyuk.userregistrationapp.util.Constants.CONFIRM_REGISTRATION_MESSAGE;
import static com.dkorolyuk.userregistrationapp.util.Constants.INVALID_INPUT_MESSAGE;
import static com.dkorolyuk.userregistrationapp.util.Constants.SUCCESS_REGISTRATION_MESSAGE;
import static com.dkorolyuk.userregistrationapp.util.Constants.UNSUCCESSFUL_REGISTRATION_MESSAGE;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Validated @RequestBody UserDto user, BindingResult bindingResult) {

        log.info("User registration called for user {}", user.name());

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest()
                    .body(buildBindingErrorMessage(bindingResult, INVALID_INPUT_MESSAGE));
        }

        return Optional.ofNullable(userService.getUser(user))
                .map(existingUser -> ResponseEntity.badRequest().body(
                        userService.buildDuplicationMessage(user, existingUser)
                ))
                .orElseGet(() -> {
                    userService.saveUser(user);
                    userService.sendEmail(user.email());
                    return ResponseEntity.status(HttpStatus.CREATED).body(CONFIRM_REGISTRATION_MESSAGE);
                });
    }


    @PatchMapping("/confirm-registration")
    public ResponseEntity<String> confirmRegistration(@RequestParam String email) {

        log.info("User registration confirmation started for user with email {}", email);

        boolean isSuccessful = userService.confirmRegistration(email);

        return isSuccessful ?
                ResponseEntity.ok(SUCCESS_REGISTRATION_MESSAGE + email) :
                ResponseEntity.badRequest().body(UNSUCCESSFUL_REGISTRATION_MESSAGE + email);
    }
}
