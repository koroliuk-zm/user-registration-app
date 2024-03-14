package com.dkorolyuk.userregistrationapp.scheduling;

import com.dkorolyuk.userregistrationapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

import static com.dkorolyuk.userregistrationapp.model.RegistrationStatus.PENDING;
import static com.dkorolyuk.userregistrationapp.util.Constants.DAYS_TO_WAIT_CONFIRMATION;
import static com.dkorolyuk.userregistrationapp.util.Constants.TIME_TO_EXECUTE_SCHEDULER;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserScheduler {

    private final UserRepository userRepository;

    @Scheduled(cron = TIME_TO_EXECUTE_SCHEDULER)
    @Transactional
    public void deleteExpiredUnconfirmedAccounts() {
        try {
            Optional.ofNullable(userRepository.findByRegistrationStatusAndRegistrationDateBefore(PENDING, LocalDate.now().minusDays(DAYS_TO_WAIT_CONFIRMATION)))
                    .filter(usersToDelete -> !usersToDelete.isEmpty())
                    .ifPresentOrElse(
                            usersToDelete -> {
                                userRepository.deleteAll(usersToDelete);
                                log.info("{} expired unconfirmed accounts deleted.", usersToDelete.size());
                            },
                            () -> log.info("No expired unconfirmed accounts found.")
                    );
        } catch (Exception e) {
            log.error("An error occurred while trying to delete expired unconfirmed accounts: {}", e.getMessage());
        }
    }
}
