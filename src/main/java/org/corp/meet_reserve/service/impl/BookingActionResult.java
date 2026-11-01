package org.corp.meet_reserve.service.impl;

import org.corp.meet_reserve.api.model.BookingResponse;

public record BookingActionResult(BookingResponse response, boolean expired) {
}
