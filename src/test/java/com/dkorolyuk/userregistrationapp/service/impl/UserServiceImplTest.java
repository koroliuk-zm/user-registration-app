package com.dkorolyuk.userregistrationapp.service.impl;

import com.dkorolyuk.userregistrationapp.dto.UserDto;
import com.dkorolyuk.userregistrationapp.model.User;
import com.dkorolyuk.userregistrationapp.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static com.dkorolyuk.userregistrationapp.model.RegistrationStatus.CONFIRMED;
import static com.dkorolyuk.userregistrationapp.model.RegistrationStatus.PENDING;
import static com.dkorolyuk.userregistrationapp.util.Constants.VALIDATION_NAME_EXISTS_MESSAGE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
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
        UserDto userDto = new UserDto("Dmytro", "dmytro@gmail.com", "password");
        User expectedUser = new User();

        when(userRepository.findByNameOrEmail(userDto.name(), userDto.email())).thenReturn(expectedUser);

        User result = userService.getUser(userDto);

        verify(userRepository).findByNameOrEmail(userDto.name(), userDto.email());

        assertThat(result).isEqualTo(expectedUser);
    }

    @Test
    void getUser_userDoesNotExist() {
        UserDto userDto = new UserDto("Dmytro", "dmytro@gmail.com", "password");

        when(userRepository.findByNameOrEmail(userDto.name(), userDto.email())).thenReturn(null);

        User result = userService.getUser(userDto);

        verify(userRepository).findByNameOrEmail(userDto.name(), userDto.email());

        assertThat(result).isNull();
    }

    @Test
    void saveUser_savesCorrectUser() {
        UserDto userDto = new UserDto("Dmytro", "dmytro@gmail.com", "password");

        userService.saveUser(userDto);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();

        assertThat(savedUser).extracting(User::getName, User::getEmail, User::getRegistrationStatus)
                .containsExactly(userDto.name(), userDto.email(), PENDING);
    }

    @Test
    void confirmRegistration_UserExists() {
        String email = "dmytro@gmail.com";
        User user = new User();

        when(userRepository.findByEmailAndRegistrationStatus(email, PENDING)).thenReturn(Optional.of(user));

        boolean result = userService.confirmRegistration(email);

        verify(userRepository).save(user);

        assertThat(user.getRegistrationStatus()).isEqualTo(CONFIRMED);
        assertThat(result).isTrue();
    }

    @Test
    void confirmRegistration_UserDoesNotExist() {
        String email = "dmytro@gmail.com";

        when(userRepository.findByEmailAndRegistrationStatus(email, PENDING)).thenReturn(Optional.empty());

        boolean result = userService.confirmRegistration(email);

        verify(userRepository, never()).save(any());

        assertThat(result).isFalse();
    }

    @Test
    void buildDuplicationMessage_userExists() {
        UserDto userDto = new UserDto("Dima", "dima@gmail.com", "password");
        User existingUser = new User(1L, "Dima", "dimas@gmail.com", "password", CONFIRMED, LocalDate.now());

        String response = userService.buildDuplicationMessage(userDto, existingUser);

        assertThat(response).contains(VALIDATION_NAME_EXISTS_MESSAGE);
    }
}