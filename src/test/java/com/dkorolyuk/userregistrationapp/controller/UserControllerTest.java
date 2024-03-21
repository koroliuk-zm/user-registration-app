package com.dkorolyuk.userregistrationapp.controller;

import com.dkorolyuk.userregistrationapp.dto.RegistrationRequest;
import com.dkorolyuk.userregistrationapp.model.Email;
import com.dkorolyuk.userregistrationapp.model.RegistrationStatus;
import com.dkorolyuk.userregistrationapp.model.User;
import com.dkorolyuk.userregistrationapp.service.EmailService;
import com.dkorolyuk.userregistrationapp.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

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

    @MockBean
    private EmailService emailService;

    @Test
    void registerUser_isCreatedWhenUserIsValid() throws Exception {
        RegistrationRequest dataToSave = new RegistrationRequest("Ivan", "ivan@gmail.com", "password");
        User savedUser = User.builder()
                .id(1L)
                .name("Ivan").build();

        when(userService.saveUser(dataToSave)).thenReturn(savedUser);

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"name\": \"Ivan\", \"email\": \"ivan@gmail.com\", \"password\": \"password\" }"))
                .andExpect(status().isCreated());

        verify(userService).saveUser(dataToSave);
        verify(emailService).sendEmail(dataToSave.email(), savedUser.getId());
    }

    @Test
    void registerUser_isBadRequestWhenUserAlreadyExists() throws Exception {
        RegistrationRequest registrationRequest = new RegistrationRequest("Andrii", "andrii@gmail.com", "password");
        Email email = Email.builder().userId(1L).emailAddress("andrii@gmail.com").build();
        User userFromDb = new User(1L, "Andrii", email, "password", RegistrationStatus.PENDING, LocalDate.now());
        email.setUser(userFromDb);

        when(userService.getUser(registrationRequest)).thenReturn(userFromDb);

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"name\": \"Andrii\", \"email\": \"andrii@gmail.com\", \"password\": \"password\"}"))
                .andExpect(status().isBadRequest());

        verify(userService).getUser(registrationRequest);
    }

    @Test
    void confirmRegistration_isOkWhenRegistrationConfirmed() throws Exception {
        String email = "oleksandr@gmail.com";
        String confirmationCode = "12345";

        when(userService.confirmRegistration(email, confirmationCode)).thenReturn(true);

        mockMvc.perform(patch("/api/users/confirm-registration")
                        .param("email", email)
                        .param("confirmationCode", confirmationCode)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(userService).confirmRegistration(email, confirmationCode);
    }

    @Test
    void confirmRegistration_isbadRequestWhenEmailNotFound() throws Exception {
        String email = "oleksandr@gmail.com";
        String confirmationCode = "12345";

        when(userService.confirmRegistration(email, confirmationCode)).thenReturn(false);

        mockMvc.perform(patch("/api/users/confirm-registration")
                        .param("email", email)
                        .param("confirmationCode", confirmationCode)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(userService).confirmRegistration(email, confirmationCode);
    }
}