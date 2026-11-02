package org.corp.meet_reserve.service.impl;

import org.corp.meet_reserve.entity.BookingStatus;
import org.corp.meet_reserve.repository.BookingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CleanupServiceImplTest {

    private static final Instant NOW = Instant.parse("2026-11-02T10:00:00Z");

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private CleanupServiceImpl cleanupService;

    @Test
    void expiresPendingBookingsAndReturnsUpdatedCount() {
        when(bookingRepository.expireOverdueBookings(
                NOW,
                BookingStatus.PENDING,
                BookingStatus.EXPIRED))
                .thenReturn(4);

        int updated = cleanupService.cleanupExpiredTimes(NOW);

        assertThat(updated).isEqualTo(4);
        verify(bookingRepository).expireOverdueBookings(
                NOW,
                BookingStatus.PENDING,
                BookingStatus.EXPIRED);
    }
}
