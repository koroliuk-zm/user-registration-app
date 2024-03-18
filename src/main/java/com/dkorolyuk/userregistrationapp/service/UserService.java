package com.dkorolyuk.userregistrationapp.service;

import com.dkorolyuk.userregistrationapp.dto.UserDto;
import com.dkorolyuk.userregistrationapp.model.User;


public interface UserService {

    User getUser(UserDto user);

    void saveUser(UserDto user);

    void sendEmail(String email);

    boolean confirmRegistration(String email);

    String buildDuplicationMessage(UserDto userDto, User existingUser);
}
