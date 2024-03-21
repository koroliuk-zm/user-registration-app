package com.dkorolyuk.userregistrationapp.service.impl;

import com.dkorolyuk.userregistrationapp.dto.RegistrationRequest;
import com.dkorolyuk.userregistrationapp.model.Email;
import com.dkorolyuk.userregistrationapp.model.RegistrationStatus;
import com.dkorolyuk.userregistrationapp.model.User;
import com.dkorolyuk.userregistrationapp.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.dkorolyuk.userregistrationapp.model.RegistrationStatus.CONFIRMED;
import static com.dkorolyuk.userregistrationapp.model.RegistrationStatus.PENDING;
import static com.dkorolyuk.userregistrationapp.util.Constants.VALIDATION_NAME_EXISTS_MESSAGE;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Test
    void getUser_userExists() {
        RegistrationRequest registrationRequest = new RegistrationRequest("Dmytro", "dmytro@gmail.com", "password");
        User expectedUser = new User();

        when(userRepository.findByNameOrEmailEmailAddressAndRegistrationStatus(registrationRequest.name(), registrationRequest.email(), CONFIRMED))
                .thenReturn(expectedUser);

        User result = userService.getUser(registrationRequest);

        verify(userRepository).findByNameOrEmailEmailAddressAndRegistrationStatus(registrationRequest.name(), registrationRequest.email(), CONFIRMED);

        assertThat(result).isEqualTo(expectedUser);
    }

    @Test
    void getUser_userDoesNotExist() {
        RegistrationRequest registrationRequest = new RegistrationRequest("Dmytro", "dmytro@gmail.com", "password");

        when(userRepository.findByNameOrEmailEmailAddressAndRegistrationStatus(registrationRequest.name(), registrationRequest.email(), CONFIRMED)).thenReturn(null);

        User result = userService.getUser(registrationRequest);

        verify(userRepository).findByNameOrEmailEmailAddressAndRegistrationStatus(registrationRequest.name(), registrationRequest.email(), CONFIRMED);

        assertThat(result).isNull();
    }

    @Test
    void saveUser_savesCorrectUser() {
        RegistrationRequest registrationRequest = new RegistrationRequest("Dmytro", "dmytro@gmail.com", "password");

        Email expectedEmail = Email.builder().emailAddress("dmytro@gmail.com").build();

        userService.saveUser(registrationRequest);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();

        assertThat(savedUser).extracting(User::getName, User::getEmail, User::getRegistrationStatus)
                .containsExactly(registrationRequest.name(), expectedEmail, PENDING);
    }

    @Test
    void confirmRegistration_UserExists() {
        String emailAddress = "dmytro@gmail.com";
        String confirmationCode = "12324";
        User user = User.builder().registrationStatus(PENDING).build();
        Email email = Email.builder().emailAddress(emailAddress).confirmationCode(confirmationCode).build();
        user.setEmail(email);

        when(userRepository.findByEmailEmailAddress(emailAddress)).thenReturn(singletonList(user));

        boolean result = userService.confirmRegistration(emailAddress, confirmationCode);

        verify(userRepository).save(user);

        assertThat(user.getRegistrationStatus()).isEqualTo(CONFIRMED);
        assertThat(result).isTrue();
    }

    @Test
    void confirmRegistration_UserDoesNotExist() {
        String email = "dmytro@gmail.com";
        String confirmationCode = "1244";

        when(userRepository.findByEmailEmailAddress(email)).thenReturn(emptyList());

        boolean result = userService.confirmRegistration(email, confirmationCode);

        verify(userRepository, never()).save(any());

        assertThat(result).isFalse();
    }

    @Test
    void buildDuplicationMessage_userExists() {
        RegistrationRequest registrationRequest = new RegistrationRequest("Dima", "dima@gmail.com", "password");
        Email email = Email.builder().userId(1L).emailAddress("dima@gmail.com").build();
        User existingUser = new User(1L, "Dima", email, "password", CONFIRMED, LocalDate.now());

        String response = userService.buildDuplicationMessage(registrationRequest, existingUser);

        assertThat(response).contains(VALIDATION_NAME_EXISTS_MESSAGE);
    }

    @Test
    void deleteExpiredUnconfirmedAccounts_Success() {
        List<User> usersToDelete = new ArrayList<>();
        Email email = Email.builder().emailAddress("user1@example.com").build();
        User user1 = new User(1L, "user1", email, "password", RegistrationStatus.PENDING, LocalDate.now().minusDays(3));
        User user2 = new User(2L, "user2", email, "password", RegistrationStatus.PENDING, LocalDate.now().minusDays(3));
        usersToDelete.add(user1);
        usersToDelete.add(user2);

        when(userRepository.findByRegistrationStatusAndRegistrationDateBefore(RegistrationStatus.PENDING, LocalDate.now().minusDays(2)))
                .thenReturn(usersToDelete);

        userService.deleteExpiredAccounts();

        verify(userRepository, times(1)).deleteAll(usersToDelete);
    }

    @Test
    void deleteExpiredUnconfirmedAccounts_NoExpiredAccounts() {
        List<User> usersToDelete = new ArrayList<>();

        when(userRepository.findByRegistrationStatusAndRegistrationDateBefore(RegistrationStatus.PENDING, LocalDate.now().minusDays(2)))
                .thenReturn(usersToDelete);

        userService.deleteExpiredAccounts();

        verify(userRepository, never()).deleteAll(anyList());
    }

    @Test
    void deleteExpiredUnconfirmedAccounts_ExceptionHandling() {
        when(userRepository.findByRegistrationStatusAndRegistrationDateBefore(RegistrationStatus.PENDING, LocalDate.now().minusDays(2)))
                .thenThrow(new RuntimeException("Database connection failed"));

        userService.deleteExpiredAccounts();

        verify(userRepository, never()).deleteAll(anyList());
    }
}