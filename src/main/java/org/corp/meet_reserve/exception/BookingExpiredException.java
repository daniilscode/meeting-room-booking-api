package org.corp.meet_reserve.exception;

public class BookingExpiredException extends RuntimeException {

    public BookingExpiredException() {
        super("Срок действия брони истёк");
    }
}
