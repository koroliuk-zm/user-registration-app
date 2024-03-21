package com.dkorolyuk.userregistrationapp.scheduling;

import com.dkorolyuk.userregistrationapp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import static com.dkorolyuk.userregistrationapp.util.Constants.TIME_TO_EXECUTE_SCHEDULER;

@Component
@RequiredArgsConstructor
public class UserScheduler {

    private final UserService userService;

    @Scheduled(cron = TIME_TO_EXECUTE_SCHEDULER)
    public void deleteExpiredUnconfirmedAccounts() {
        userService.deleteExpiredAccounts();
    }
}
