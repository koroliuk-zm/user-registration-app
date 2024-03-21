package com.dkorolyuk.userregistrationapp.repository;

import com.dkorolyuk.userregistrationapp.model.RegistrationStatus;
import com.dkorolyuk.userregistrationapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByNameOrEmailEmailAddressAndRegistrationStatus(String name, String email, RegistrationStatus registrationStatus);

    List<User> findByEmailEmailAddress(String email);

    List<User> findByRegistrationStatusAndRegistrationDateBefore(RegistrationStatus status, LocalDate registrationDate);

    void deleteAll(Iterable<? extends User> users);
}

