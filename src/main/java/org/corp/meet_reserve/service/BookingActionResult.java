package org.corp.meet_reserve.service;

import org.corp.meet_reserve.api.model.BookingResponse;

/**
 * Содержит результат перехода состояния брони.
 */
public record BookingActionResult(BookingResponse response, boolean expired) {
}
