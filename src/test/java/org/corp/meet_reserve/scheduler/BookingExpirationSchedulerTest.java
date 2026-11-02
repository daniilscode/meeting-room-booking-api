package org.corp.meet_reserve.scheduler;

import org.corp.meet_reserve.service.CleanupService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingExpirationSchedulerTest {

    private static final Instant NOW = Instant.parse("2026-11-02T10:00:00Z");

    @Mock
    private CleanupService cleanupService;

    @Test
    void passesCurrentTimeToCleanupService() {
        Clock clock = Clock.fixed(NOW, ZoneOffset.UTC);
        BookingExpirationScheduler scheduler = new BookingExpirationScheduler(clock, cleanupService);
        when(cleanupService.cleanupExpiredTimes(NOW)).thenReturn(3);

        scheduler.expireOverdueBookings();

        verify(cleanupService).cleanupExpiredTimes(NOW);
    }

    @Test
    void doesNotPropagateCleanupFailure() {
        Clock clock = Clock.fixed(NOW, ZoneOffset.UTC);
        BookingExpirationScheduler scheduler = new BookingExpirationScheduler(clock, cleanupService);
        when(cleanupService.cleanupExpiredTimes(NOW))
                .thenThrow(new IllegalStateException("database unavailable"));

        assertThatCode(scheduler::expireOverdueBookings)
                .doesNotThrowAnyException();
    }
}
