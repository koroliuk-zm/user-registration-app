package com.dkorolyuk.userregistrationapp.scheduling;

import com.dkorolyuk.userregistrationapp.model.RegistrationStatus;
import com.dkorolyuk.userregistrationapp.model.User;
import com.dkorolyuk.userregistrationapp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserSchedulerTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserScheduler userScheduler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void deleteExpiredUnconfirmedAccounts_Success() {
        List<User> usersToDelete = new ArrayList<>();
        User user1 = new User(1L, "user1", "user1@example.com", "password", RegistrationStatus.PENDING, LocalDate.now().minusDays(3));
        User user2 = new User(2L, "user2", "user2@example.com", "password", RegistrationStatus.PENDING, LocalDate.now().minusDays(3));
        usersToDelete.add(user1);
        usersToDelete.add(user2);

        when(userRepository.findByRegistrationStatusAndRegistrationDateBefore(RegistrationStatus.PENDING, LocalDate.now().minusDays(2)))
                .thenReturn(usersToDelete);

        userScheduler.deleteExpiredUnconfirmedAccounts();

        verify(userRepository, times(1)).deleteAll(usersToDelete);
    }

    @Test
    void deleteExpiredUnconfirmedAccounts_NoExpiredAccounts() {
        List<User> usersToDelete = new ArrayList<>();

        when(userRepository.findByRegistrationStatusAndRegistrationDateBefore(RegistrationStatus.PENDING, LocalDate.now().minusDays(2)))
                .thenReturn(usersToDelete);

        userScheduler.deleteExpiredUnconfirmedAccounts();

        verify(userRepository, never()).deleteAll(anyList());
    }

    @Test
    void deleteExpiredUnconfirmedAccounts_ExceptionHandling() {
        when(userRepository.findByRegistrationStatusAndRegistrationDateBefore(RegistrationStatus.PENDING, LocalDate.now().minusDays(2)))
                .thenThrow(new RuntimeException("Database connection failed"));

        userScheduler.deleteExpiredUnconfirmedAccounts();

        verify(userRepository, never()).deleteAll(anyList());
    }
}
