package com.dkorolyuk.userregistrationapp.controller;

import com.dkorolyuk.userregistrationapp.dto.UserDto;
import com.dkorolyuk.userregistrationapp.model.RegistrationStatus;
import com.dkorolyuk.userregistrationapp.model.User;
import com.dkorolyuk.userregistrationapp.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    void registerUser_isCreatedWhenUserIsValid() throws Exception {
        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"name\": \"Ivan\", \"email\": \"ivan@gmail.com\", \"password\": \"password\", \"passwordConfirm\": \"password\" }"))
                .andExpect(status().isCreated());

        verify(userService).saveUser(new UserDto("Ivan", "ivan@gmail.com", "password", "password"));
    }

    @Test
    void registerUser_isBadRequestWhenPasswordsNotMatch() throws Exception {
        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"name\": \"Dmytro\", \"email\": \"dmytro@gmail.com\", \"password\": \"12345678\", \"passwordConfirm\": \"87654321\" }"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registerUser_isBadRequestWhenRegistrationNotConfirmed() throws Exception {
        UserDto userDto = new UserDto("Andrii", "andrii@gmail.com", "password", "password");
        User userFromDb = new User(1L, "Andrii", "andrii@gmail.com", "password", RegistrationStatus.PENDING, LocalDate.now());

        when(userService.getUser(userDto)).thenReturn(userFromDb);

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"name\": \"Andrii\", \"email\": \"andrii@gmail.com\", \"password\": \"password\", \"passwordConfirm\": \"password\" }"))
                .andExpect(status().isBadRequest());

        verify(userService).getUser(userDto);
    }

    @Test
    void confirmRegistration_isOkWhenRegistrationConfirmed() throws Exception {
        String email = "oleksandr@gmail.com";

        when(userService.confirmRegistration(email)).thenReturn(true);

        mockMvc.perform(patch("/api/users/confirm-registration")
                        .param("email", email)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(userService).confirmRegistration(email);
    }

    @Test
    void confirmRegistration_isbadRequestWhenEmailNotFound() throws Exception {
        String email = "oleksandr@gmail.com";

        when(userService.confirmRegistration(email)).thenReturn(false);

        mockMvc.perform(patch("/api/users/confirm-registration")
                        .param("email", email)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(userService).confirmRegistration(email);
    }
}