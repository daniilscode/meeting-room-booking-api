package org.corp.meet_reserve.exception;

import jakarta.validation.ConstraintViolationException;
import org.corp.meet_reserve.api.model.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.UUID;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final Clock clock;

    public GlobalExceptionHandler(Clock clock) {
        this.clock = clock;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .orElse("Некорректные данные запроса");
        return error(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", message);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<ErrorResponse> handleUnreadableMessage() {
        return error(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "Некорректный JSON или формат даты");
    }

    @ExceptionHandler(ConstraintViolationException.class)
    ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException exception) {
        return error(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", exception.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException exception) {
        return error(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException exception) {
        String message = UUID.class.equals(exception.getRequiredType())
                ? "Некорректный UUID брони"
                : "Некорректный идентификатор ресурса";
        return error(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", message);
    }

    @ExceptionHandler(BookingNotFoundException.class)
    ResponseEntity<ErrorResponse> handleBookingNotFound(BookingNotFoundException exception) {
        return error(HttpStatus.NOT_FOUND, "BOOKING_NOT_FOUND", exception.getMessage());
    }

    @ExceptionHandler(BookingNotPendingException.class)
    ResponseEntity<ErrorResponse> handleBookingNotPending(BookingNotPendingException exception) {
        return error(HttpStatus.CONFLICT, "BOOKING_NOT_PENDING", exception.getMessage());
    }

    @ExceptionHandler(BookingExpiredException.class)
    ResponseEntity<ErrorResponse> handleBookingExpired(BookingExpiredException exception) {
        return error(HttpStatus.GONE, "BOOKING_EXPIRED", exception.getMessage());
    }

    @ExceptionHandler(RoomNotFoundException.class)
    ResponseEntity<ErrorResponse> handleRoomNotFound(RoomNotFoundException exception) {
        return error(HttpStatus.NOT_FOUND, "ROOM_NOT_FOUND", exception.getMessage());
    }

    @ExceptionHandler(RoomInactiveException.class)
    ResponseEntity<ErrorResponse> handleInactiveRoom(RoomInactiveException exception) {
        return error(HttpStatus.CONFLICT, "ROOM_INACTIVE", exception.getMessage());
    }

    @ExceptionHandler(RoomAlreadyBookedException.class)
    ResponseEntity<ErrorResponse> handleBookedRoom(RoomAlreadyBookedException exception) {
        return error(HttpStatus.CONFLICT, "SLOT_ALREADY_BOOKED", exception.getMessage());
    }

    @ExceptionHandler(RoomAlreadyExistsException.class)
    ResponseEntity<ErrorResponse> handleRoomAlreadyExists(RoomAlreadyExistsException exception) {
        return error(HttpStatus.CONFLICT, "ROOM_ALREADY_EXISTS", exception.getMessage());
    }

    private ResponseEntity<ErrorResponse> error(HttpStatus status, String code, String message) {
        return ResponseEntity.status(status)
                .body(new ErrorResponse(code, message, OffsetDateTime.now(clock)));
    }
}
