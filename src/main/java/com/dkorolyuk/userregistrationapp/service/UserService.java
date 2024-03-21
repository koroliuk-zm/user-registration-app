package com.dkorolyuk.userregistrationapp.service;

import com.dkorolyuk.userregistrationapp.dto.RegistrationRequest;
import com.dkorolyuk.userregistrationapp.model.User;


public interface UserService {

    User getUser(RegistrationRequest user);

    User saveUser(RegistrationRequest user);

    boolean confirmRegistration(String email, String confirmationCode);

    String buildDuplicationMessage(RegistrationRequest registrationRequest, User existingUser);

    void deleteExpiredAccounts();
}
