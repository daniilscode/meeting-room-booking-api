package org.corp.meet_reserve.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.corp.meet_reserve.service.CleanupService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Instant;

import static org.corp.meet_reserve.util.MessagesUtil.CLEAN_COMPLETE;
import static org.corp.meet_reserve.util.MessagesUtil.CLEAN_ERROR;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookingExpirationScheduler {

    private final Clock clock;
    private final CleanupService cleanupService;

    @Scheduled(fixedDelayString = "${app.cleanup.fixed-delay-ms}")
    public void expireOverdueBookings() {
        Instant startedAt = clock.instant();

        try {
            int updated = cleanupService.cleanupExpiredTimes(startedAt);

            if (updated > 0) {
                log.atInfo().setMessage(CLEAN_COMPLETE).addArgument(updated).addArgument(startedAt).log();
            }
        } catch (Exception exception) {
            log.atError()
                    .setMessage(CLEAN_ERROR)
                    .addArgument(startedAt)
                    .setCause(exception)
                    .log();
        }
    }
}
