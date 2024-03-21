package com.dkorolyuk.userregistrationapp.service;

public interface EmailService {
    void sendEmail(String email, Long userId);
}
