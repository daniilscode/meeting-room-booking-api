package org.corp.meet_reserve.exception;

import org.corp.meet_reserve.api.model.ErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Clock;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private static final Instant NOW = Instant.parse("2026-11-03T10:00:00Z");

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler(Clock.fixed(NOW, ZoneOffset.UTC));
    }

    @Test
    void mapsRoomAlreadyExistsToConflict() {
        ResponseEntity<ErrorResponse> response = handler.handleRoomAlreadyExists(
                new RoomAlreadyExistsException("Большая переговорная"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo("ROOM_ALREADY_EXISTS");
        assertThat(response.getBody().getMessage())
                .isEqualTo("Комната с таким именем уже существует: Большая переговорная");
        assertThat(response.getBody().getTimestamp())
                .isEqualTo(OffsetDateTime.ofInstant(NOW, ZoneOffset.UTC));
    }

    @Test
    void mapsRoomNotFoundToNotFound() {
        ResponseEntity<ErrorResponse> response = handler.handleRoomNotFound(
                new RoomNotFoundException(42L));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo("ROOM_NOT_FOUND");
        assertThat(response.getBody().getMessage()).isEqualTo("Комната не найдена: 42");
    }

    @Test
    void mapsInactiveRoomToConflict() {
        ResponseEntity<ErrorResponse> response = handler.handleInactiveRoom(
                new RoomInactiveException());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo("ROOM_INACTIVE");
    }
}
