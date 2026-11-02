package org.corp.meet_reserve.service.impl;

import lombok.RequiredArgsConstructor;
import org.corp.meet_reserve.entity.Booking;
import org.corp.meet_reserve.exception.BookingExpiredException;
import org.corp.meet_reserve.exception.BookingNotFoundException;
import org.corp.meet_reserve.exception.BookingNotPendingException;
import org.corp.meet_reserve.repository.BookingRepository;
import org.corp.meet_reserve.service.BookingActionResult;
import org.corp.meet_reserve.service.BookingLifecycleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

import static org.corp.meet_reserve.entity.BookingStatus.CANCELLED;
import static org.corp.meet_reserve.entity.BookingStatus.CONFIRMED;
import static org.corp.meet_reserve.entity.BookingStatus.EXPIRED;
import static org.corp.meet_reserve.entity.BookingStatus.PENDING;

/**
 * Реализует атомарные переходы состояний брони.
 */
@Service
@RequiredArgsConstructor
public class BookingLifecycleServiceImpl implements BookingLifecycleService {

    private final BookingRepository bookingRepository;
    private final Clock clock;

    @Override
    @Transactional
    public BookingActionResult confirm(UUID bookingId) {
        Booking booking = getForUpdate(bookingId);

        if (booking.getStatus() == CONFIRMED) {
            return new BookingActionResult(BookingResponseMapper.toResponse(booking), false);
        }
        if (booking.getStatus() == CANCELLED) {
            throw new BookingNotPendingException();
        }
        if (booking.getStatus() == EXPIRED) {
            throw new BookingExpiredException();
        }

        if (isExpired(booking)) {
            booking.setStatus(EXPIRED);
            bookingRepository.flush();
            return new BookingActionResult(null, true);
        }

        booking.setStatus(CONFIRMED);
        booking.setExpiresAt(null);
        bookingRepository.flush();
        return new BookingActionResult(BookingResponseMapper.toResponse(booking), false);
    }

    @Override
    @Transactional
    public BookingActionResult cancel(UUID bookingId) {
        Booking booking = getForUpdate(bookingId);

        if (booking.getStatus() == CANCELLED) {
            return new BookingActionResult(null, false);
        }
        if (booking.getStatus() == EXPIRED) {
            throw new BookingExpiredException();
        }
        if (booking.getStatus() == PENDING && isExpired(booking)) {
            booking.setStatus(EXPIRED);
            bookingRepository.flush();
            return new BookingActionResult(null, true);
        }

        booking.setStatus(CANCELLED);
        booking.setExpiresAt(null);
        bookingRepository.flush();
        return new BookingActionResult(null, false);
    }

    private Booking getForUpdate(UUID bookingId) {
        return bookingRepository.findByIdForUpdate(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(bookingId));
    }

    private boolean isExpired(Booking booking) {
        Instant expiresAt = booking.getExpiresAt();
        return expiresAt != null && !expiresAt.isAfter(clock.instant());
    }
}
