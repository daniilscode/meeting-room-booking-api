package org.corp.meet_reserve.service;

import org.corp.meet_reserve.api.model.CreateRoomRequest;
import org.corp.meet_reserve.api.model.RoomResponse;

import java.util.List;

/**
 * Управляет переговорными комнатами.
 */
public interface RoomService {

    /**
     * Создаёт новую переговорную комнату.
     */
    RoomResponse createRoom(CreateRoomRequest request);

    /**
     * Возвращает список комнат.
     */
    List<RoomResponse> getRooms(boolean activeOnly);

    /**
     * Возвращает комнату по идентификатору.
     */
    RoomResponse getRoom(Long roomId);

    /**
     * Деактивирует комнату, сохраняя её историю.
     */
    RoomResponse deactivateRoom(Long roomId);
}
