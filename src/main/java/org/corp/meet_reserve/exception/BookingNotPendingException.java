package org.corp.meet_reserve.exception;

public class BookingNotPendingException extends RuntimeException {

    public BookingNotPendingException() {
        super("Бронь нельзя подтвердить в текущем статусе");
    }
}
