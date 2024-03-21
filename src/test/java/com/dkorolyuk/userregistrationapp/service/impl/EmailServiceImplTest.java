package com.dkorolyuk.userregistrationapp.service.impl;

import com.dkorolyuk.userregistrationapp.exception.UserNotFoundException;
import com.dkorolyuk.userregistrationapp.model.Email;
import com.dkorolyuk.userregistrationapp.repository.EmailRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmailServiceImplTest {

    @Mock
    private EmailRepository emailRepository;

    @InjectMocks
    private EmailServiceImpl emailService;

    @Test
    void sendEmail_Successfully() {
        String emailAddress = "test@example.com";
        Long userId = 123L;

        Email email = new Email();
        email.setEmailAddress(emailAddress);
        email.setUserId(userId);

        when(emailRepository.findByEmailAddressAndUserId(emailAddress, userId)).thenReturn(Optional.of(email));

        emailService.sendEmail(emailAddress, userId);

        ArgumentCaptor<Email> emailCaptor = ArgumentCaptor.forClass(Email.class);
        verify(emailRepository, times(1)).save(emailCaptor.capture());

        assertThat(emailCaptor.getValue().getEmailAddress()).isEqualTo(emailAddress);
        assertThat(emailCaptor.getValue().getConfirmationCode()).isNotNull();
    }

    @Test
    void sendEmail_UserNotFound() {
        String emailAddress = "test@example.com";
        Long userId = 123L;

        when(emailRepository.findByEmailAddressAndUserId(emailAddress, userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> emailService.sendEmail(emailAddress, userId));

        verify(emailRepository, never()).save(any(Email.class));
    }
}
