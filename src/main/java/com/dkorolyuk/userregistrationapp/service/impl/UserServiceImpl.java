package com.dkorolyuk.userregistrationapp.service.impl;

import com.dkorolyuk.userregistrationapp.dto.RegistrationRequest;
import com.dkorolyuk.userregistrationapp.model.Email;
import com.dkorolyuk.userregistrationapp.model.User;
import com.dkorolyuk.userregistrationapp.repository.UserRepository;
import com.dkorolyuk.userregistrationapp.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.dkorolyuk.userregistrationapp.model.RegistrationStatus.CONFIRMED;
import static com.dkorolyuk.userregistrationapp.model.RegistrationStatus.PENDING;
import static com.dkorolyuk.userregistrationapp.util.Constants.DAYS_TO_WAIT_CONFIRMATION;
import static com.dkorolyuk.userregistrationapp.util.Constants.VALIDATION_EMAIL_EXISTS_MESSAGE;
import static com.dkorolyuk.userregistrationapp.util.Constants.VALIDATION_NAME_EXISTS_MESSAGE;
import static com.dkorolyuk.userregistrationapp.util.Constants.VALIDATION_PENDING_STATUS_MESSAGE;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public User getUser(RegistrationRequest user) {
        return userRepository.findByNameOrEmailEmailAddressAndRegistrationStatus(user.name(), user.email(), CONFIRMED);
    }

    @Override
    @Transactional
    public User saveUser(RegistrationRequest registrationRequest) {
        User user = User.builder()
                .name(registrationRequest.name())
                .password(hashPassword(registrationRequest.password()))
                .registrationStatus(PENDING)
                .registrationDate(LocalDate.now()).build();

        Email email = Email.builder()
                .emailAddress(registrationRequest.email())
                .user(user).build();

        user.setEmail(email);

        return userRepository.save(user);
    }

    @Override
    @Transactional
    public boolean confirmRegistration(String email, String confirmationCode) {
        List<User> usersByEmail = userRepository.findByEmailEmailAddress(email);
        boolean isEmailAlreadyConfirmed = usersByEmail.stream()
                .anyMatch(user -> user.getRegistrationStatus() == CONFIRMED);

        if (isEmailAlreadyConfirmed) {
            return false;
        }

        return usersByEmail.stream()
                    .filter(user -> user.getEmail().getConfirmationCode().equals(confirmationCode) && user.getRegistrationStatus() == PENDING)
                    .findFirst()
                    .map(user -> {
                        user.setRegistrationStatus(CONFIRMED);
                        userRepository.save(user);
                        return true;
                    })
                    .orElse(false);
    }

    @Override
    public String buildDuplicationMessage(RegistrationRequest registrationRequest, User existingUser) {
        StringBuilder builder = new StringBuilder();
        buildErrorMessage(existingUser.getRegistrationStatus(), PENDING, builder, VALIDATION_PENDING_STATUS_MESSAGE);
        buildErrorMessage(existingUser.getName(), registrationRequest.name(), builder, VALIDATION_NAME_EXISTS_MESSAGE);
        buildErrorMessage(existingUser.getEmail().getEmailAddress(), registrationRequest.email(), builder, VALIDATION_EMAIL_EXISTS_MESSAGE);
        return builder.toString();
    }


    @Override
    @Transactional
    public void deleteExpiredAccounts() {
        try {
            Optional.ofNullable(userRepository.findByRegistrationStatusAndRegistrationDateBefore(PENDING, LocalDate.now().minusDays(DAYS_TO_WAIT_CONFIRMATION)))
                    .filter(usersToDelete -> !usersToDelete.isEmpty())
                    .ifPresentOrElse(
                            usersToDelete -> {
                                userRepository.deleteAll(usersToDelete);
                                log.info("{} expired unconfirmed accounts deleted.", usersToDelete.size());
                            },
                            () -> log.info("No expired unconfirmed accounts found.")
                    );
        } catch (Exception e) {
            log.error("An error occurred while trying to delete expired unconfirmed accounts: {}", e.getMessage());
        }
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
