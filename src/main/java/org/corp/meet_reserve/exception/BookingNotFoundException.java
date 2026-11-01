package org.corp.meet_reserve.exception;

import java.util.UUID;

public class BookingNotFoundException extends RuntimeException {

    public BookingNotFoundException(UUID bookingId) {
        super("Бронь не найдена: " + bookingId);
    }
}
