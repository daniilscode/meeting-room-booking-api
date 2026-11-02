package org.corp.meet_reserve.service;

import java.util.UUID;

/**
 * Выполняет атомарные переходы брони между состояниями.
 */
public interface BookingLifecycleService {

    /**
     * Подтверждает бронь или фиксирует её истечение.
     */
    BookingActionResult confirm(UUID bookingId);

    /**
     * Отменяет бронь или фиксирует её истечение.
     */
    BookingActionResult cancel(UUID bookingId);
}
