package com.dkorolyuk.userregistrationapp.handler;

import com.dkorolyuk.userregistrationapp.dto.UserDto;
import com.dkorolyuk.userregistrationapp.model.User;

public interface UserHandler {

    String buildDuplicationMessage(UserDto userDto, User existingUser);
}
