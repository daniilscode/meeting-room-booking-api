package org.corp.meet_reserve.service;

import org.corp.meet_reserve.api.model.BookingResponse;
import org.corp.meet_reserve.api.model.CreateBookingRequest;

import java.util.UUID;

/**
 * Управляет созданием и изменением бронирований.
 */
public interface BookingService {

    /**
     * Создаёт временную бронь со статусом PENDING.
     */
    BookingResponse createPendingBooking(CreateBookingRequest request);

    /**
     * Подтверждает временную бронь.
     */
    BookingResponse confirmBooking(UUID bookingId);

    /**
     * Отменяет бронь.
     */
    void cancelBooking(UUID bookingId);
}
