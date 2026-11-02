package org.corp.meet_reserve.controller;

import lombok.RequiredArgsConstructor;
import org.corp.meet_reserve.api.RoomsApi;
import org.corp.meet_reserve.api.model.CreateRoomRequest;
import org.corp.meet_reserve.api.model.RoomResponse;
import org.corp.meet_reserve.service.RoomService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class RoomController implements RoomsApi {

    private final RoomService service;

    @Override
    public ResponseEntity<RoomResponse> createRoom(CreateRoomRequest createRoomRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createRoom(createRoomRequest));
    }

    @Override
    public ResponseEntity<RoomResponse> deactivateRoom(Long id) {
        return ResponseEntity.ok(service.deactivateRoom(id));
    }

    @Override
    public ResponseEntity<RoomResponse> getRoom(Long id) {
        return ResponseEntity.ok(service.getRoom(id));
    }

    @Override
    public ResponseEntity<List<RoomResponse>> getRooms(Boolean activeOnly) {
        boolean onlyActive = activeOnly == null || activeOnly;
        return ResponseEntity.ok(service.getRooms(onlyActive));
    }
}
