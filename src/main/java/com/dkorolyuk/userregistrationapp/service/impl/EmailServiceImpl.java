package com.dkorolyuk.userregistrationapp.service.impl;

import com.dkorolyuk.userregistrationapp.exception.UserNotFoundException;
import com.dkorolyuk.userregistrationapp.model.Email;
import com.dkorolyuk.userregistrationapp.repository.EmailRepository;
import com.dkorolyuk.userregistrationapp.service.EmailService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;

import static com.dkorolyuk.userregistrationapp.util.Constants.CHARACTERS;
import static com.dkorolyuk.userregistrationapp.util.Constants.CONFIRMATION_CODE_LENGTH;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final EmailRepository emailRepository;
    @Override
    @Transactional
    public void sendEmail(String emailAddress, Long userId) {
        String confirmationCode = generateConfirmationCode();

        Email email = emailRepository.findByEmailAddressAndUserId(emailAddress, userId)
                .orElseThrow(() -> new UserNotFoundException("There is no user with such email " + emailAddress));

        email.setConfirmationCode(confirmationCode);
        emailRepository.save(email);

        String confirmationLink = constructConfirmationLink(emailAddress, confirmationCode);
        log.info("Please confirm your registration by visiting: {}", confirmationLink);
    }

    private String generateConfirmationCode() {
        StringBuilder sb = new StringBuilder(CONFIRMATION_CODE_LENGTH);
        ThreadLocalRandom random = ThreadLocalRandom.current();
        random.ints(CONFIRMATION_CODE_LENGTH, 0, CHARACTERS.length())
                .forEach(index -> sb.append(CHARACTERS.charAt(index)));
        return sb.toString();
    }

    private String constructConfirmationLink(String emailAddress, String confirmationCode) {
        // Assuming a REST API endpoint for confirmation, construct the link
        return "/api/users/confirm-registration?email=" + emailAddress + "&confirmationCode=" + confirmationCode;
    }
}
