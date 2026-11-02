package org.corp.meet_reserve.exception;

import static org.corp.meet_reserve.util.MessagesUtil.ROOM_ALREADY_EXISTS;

public class RoomAlreadyExistsException extends RuntimeException {
    public RoomAlreadyExistsException(String roomName) {
        super(ROOM_ALREADY_EXISTS + roomName);
    }
}
