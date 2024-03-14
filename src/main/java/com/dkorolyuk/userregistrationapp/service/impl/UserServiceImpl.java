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
    public void confirmRegistration(String email) {
        userRepository.findByEmail(email)
                .ifPresentOrElse(user -> {
                    user.setRegistrationStatus(CONFIRMED);
                    userRepository.save(user);
                }, () -> log.warn("User with email {} not found during confirmation.", email));
    }

    private String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }
}
