package org.corp.meet_reserve.exception;

import static org.corp.meet_reserve.util.MessagesUtil.BOOKING_NOT_PENDING;

public class BookingNotPendingException extends RuntimeException {

    public BookingNotPendingException() {
        super(BOOKING_NOT_PENDING);
    }
}
