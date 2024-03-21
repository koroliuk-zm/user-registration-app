package com.dkorolyuk.userregistrationapp.repository;

import com.dkorolyuk.userregistrationapp.model.Email;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailRepository extends JpaRepository<Email, Long> {
    Optional<Email> findByEmailAddressAndUserId(String email, Long userId);
}
