package org.corp.meet_reserve.service.impl;

import lombok.RequiredArgsConstructor;
import org.corp.meet_reserve.api.model.CreateRoomRequest;
import org.corp.meet_reserve.api.model.RoomResponse;
import org.corp.meet_reserve.entity.Room;
import org.corp.meet_reserve.exception.RoomAlreadyExistsException;
import org.corp.meet_reserve.exception.RoomNotFoundException;
import org.corp.meet_reserve.repository.RoomRepository;
import org.corp.meet_reserve.service.RoomService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneOffset;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;

    @Override
    @Transactional
    public RoomResponse createRoom(CreateRoomRequest request) {
        String name = request.getName().trim();

        if (roomRepository.existsByName(name)) {
            throw new RoomAlreadyExistsException(name);
        }

        Room room = new Room();
        room.setName(request.getName().trim());
        room.setCapacity(request.getCapacity());
        room.setHasProjector(Boolean.TRUE.equals(request.getHasProjector()));

        try {
            Room savedRoom = roomRepository.saveAndFlush(room);
            return toResponse(savedRoom);
        } catch (DataIntegrityViolationException exception) {
            throw new RoomAlreadyExistsException(room.getName());
        }

    }

    @Override
    @Transactional(readOnly = true)
    public List<RoomResponse> getRooms(boolean activeOnly) {
        List<Room> rooms = activeOnly ? roomRepository.findAllByActiveTrue() : roomRepository.findAll();
        return rooms.stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public RoomResponse getRoom(Long roomId) {
        return toResponse(roomRepository.findById(roomId).orElseThrow(() -> new RoomNotFoundException(roomId)));
    }

    @Override
    @Transactional
    public RoomResponse deactivateRoom(Long roomId) {
        Room room = roomRepository.findById(roomId).orElseThrow(() -> new RoomNotFoundException(roomId));
        room.setActive(false);
        return toResponse(room);
    }

    private RoomResponse toResponse(Room room) {
        RoomResponse response = new RoomResponse();

        response.setId(room.getId());
        response.setName(room.getName());
        response.setCapacity(room.getCapacity());
        response.setHasProjector(room.isHasProjector());
        response.setActive(room.isActive());

        if (room.getCreatedAt() != null) {
            response.setCreatedAt(
                    room.getCreatedAt().atOffset(ZoneOffset.UTC)
            );
        }

        return response;
    }
}
