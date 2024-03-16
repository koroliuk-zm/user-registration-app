package com.dkorolyuk.userregistrationapp.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;

@Entity
@Table(name = "USERS")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "USERNAME", nullable = false, unique = true)
    private String name;

    @Column(name = "EMAIL", nullable = false, unique = true)
    private String email;

    @Column(name = "PASSWORD", nullable = false)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private String password;

    @Enumerated
    @Column(name = "REGISTRATION_STATUS_ID", nullable = false)
    @EqualsAndHashCode.Exclude
    private RegistrationStatus registrationStatus;

    @Temporal(TemporalType.DATE)
    @Column(name = "REGISTRATION_DATE", nullable = false)
    @EqualsAndHashCode.Exclude
    private LocalDate registrationDate;
}
