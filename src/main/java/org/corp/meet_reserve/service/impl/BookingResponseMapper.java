package org.corp.meet_reserve.service.impl;

import org.corp.meet_reserve.api.model.BookingResponse;
import org.corp.meet_reserve.entity.Booking;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

final class BookingResponseMapper {

    private BookingResponseMapper() {
    }

    static BookingResponse toResponse(Booking booking) {
        return new BookingResponse(
                booking.getId(),
                booking.getRoom().getId(),
                booking.getUserId(),
                booking.getTitle(),
                toOffsetDateTime(booking.getStartTime()),
                toOffsetDateTime(booking.getEndTime()),
                BookingResponse.StatusEnum.valueOf(booking.getStatus().name()),
                toOffsetDateTime(booking.getExpiresAt()),
                toOffsetDateTime(booking.getCreatedAt()));
    }

    private static OffsetDateTime toOffsetDateTime(Instant value) {
        return value == null ? null : value.atOffset(ZoneOffset.UTC);
    }
}
