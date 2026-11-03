package org.corp.meet_reserve.exception;

import java.util.UUID;

import static org.corp.meet_reserve.util.MessagesUtil.BOOKING_NOT_FOUND;

public class BookingNotFoundException extends RuntimeException {

    public BookingNotFoundException(UUID bookingId) {
        super(BOOKING_NOT_FOUND + bookingId);
    }
}
