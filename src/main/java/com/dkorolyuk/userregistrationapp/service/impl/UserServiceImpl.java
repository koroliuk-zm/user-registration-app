package com.dkorolyuk.userregistrationapp.service.impl;

import com.dkorolyuk.userregistrationapp.dto.UserDto;
import com.dkorolyuk.userregistrationapp.model.User;
import com.dkorolyuk.userregistrationapp.repository.UserRepository;
import com.dkorolyuk.userregistrationapp.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static com.dkorolyuk.userregistrationapp.model.RegistrationStatus.CONFIRMED;
import static com.dkorolyuk.userregistrationapp.model.RegistrationStatus.PENDING;
import static com.dkorolyuk.userregistrationapp.util.Constants.EMAIL_CONFIRMATION_API_LINK;
import static com.dkorolyuk.userregistrationapp.util.Constants.VALIDATION_EMAIL_EXISTS_MESSAGE;
import static com.dkorolyuk.userregistrationapp.util.Constants.VALIDATION_PENDING_STATUS_MESSAGE;
import static com.dkorolyuk.userregistrationapp.util.Constants.VALIDATION_NAME_EXISTS_MESSAGE;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;


    @Override
    @Transactional(readOnly = true)
    public User getUser(UserDto user) {
        return userRepository.findByNameOrEmail(user.name(), user.email());

    }

    @Override
    @Transactional
    public void saveUser(UserDto userDto) {
        User user = User.builder()
                .name(userDto.name())
                .email(userDto.email())
                .password(hashPassword(userDto.password()))
                .registrationStatus(PENDING)
                .registrationDate(LocalDate.now()).build();

        userRepository.save(user);
    }

    @Override
    public void sendEmail(String email) {
        log.info("Please confirm your registration by calling api: {}{}", EMAIL_CONFIRMATION_API_LINK, email);
    }

    @Override
    @Transactional
    public boolean confirmRegistration(String email) {
        return userRepository.findByEmailAndRegistrationStatus(email, PENDING)
                .map(user -> {
                    user.setRegistrationStatus(CONFIRMED);
                    userRepository.save(user);
                    return true;
                })
                .orElse(false);
    }

    @Override
    public String buildDuplicationMessage(UserDto userDto, User existingUser) {
        StringBuilder builder = new StringBuilder();
        buildErrorMessage(existingUser.getRegistrationStatus(), PENDING, builder, VALIDATION_PENDING_STATUS_MESSAGE);
        buildErrorMessage(existingUser.getName(), userDto.name(), builder, VALIDATION_NAME_EXISTS_MESSAGE);
        buildErrorMessage(existingUser.getEmail(), userDto.email(), builder, VALIDATION_EMAIL_EXISTS_MESSAGE);
        return builder.toString();
    }

    private void buildErrorMessage(Object o1, Object o2, StringBuilder builder, String errorMessage) {
        if (o1.equals(o2)) {
            builder.append(errorMessage).append("; ");
        }
    }

    private String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }
}
