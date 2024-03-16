package com.dkorolyuk.userregistrationapp.util;

public class Constants {

    private Constants(){}

    public static final String CONFIRM_REGISTRATION_MESSAGE = "Please check your email to complete the registration";
    public static final String SUCCESS_REGISTRATION_MESSAGE = "The registration successfully completed for user with email ";
    public static final String UNSUCCESSFUL_REGISTRATION_MESSAGE = "There is no in db user with such email  ";
    public static final String INVALID_INPUT_MESSAGE = "Input data is not valid. Please correct your data: ";

    public static final String PASSWORD_REGEXP = "^[a-zA-Z0-9!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]*$";
    public static final String NAME_REGEXP = "^[a-zA-Z0-9]*$";
    public static final String EMAIL_REGEXP = "^(.+)@(.+)$";

    public static final String TIME_TO_EXECUTE_SCHEDULER = "0 0 0 * * *";
    public static final int DAYS_TO_WAIT_CONFIRMATION = 2;

    public static final String VALIDATION_PENDING_STATUS_MESSAGE = "Please confirm your registration. If registration will not be confirmed within two days the account will be deleted";
    public static final String VALIDATION_USER_EXISTS_MESSAGE = "User with such name already exists";
    public static final String VALIDATION_EMAIL_EXISTS_MESSAGE = "User with such email already exists";

    public static final String EMAIL_CONFIRMATION_API_LINK = "/api/users/confirm-registration?email=";
}
