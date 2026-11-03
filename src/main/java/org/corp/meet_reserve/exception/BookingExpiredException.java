package org.corp.meet_reserve.exception;

import static org.corp.meet_reserve.util.MessagesUtil.BOOKING_EXPIRED;

public class BookingExpiredException extends RuntimeException {

    public BookingExpiredException() {
        super(BOOKING_EXPIRED);
    }
}
