package org.corp.meet_reserve.service;

import org.corp.meet_reserve.api.model.BookingResponse;
import org.corp.meet_reserve.api.model.CreateBookingRequest;

import java.util.UUID;

public interface BookingService {
    BookingResponse createPendingBooking(CreateBookingRequest request);

    BookingResponse confirmBooking(UUID bookingId);

    void cancelBooking(UUID bookingId);
}
