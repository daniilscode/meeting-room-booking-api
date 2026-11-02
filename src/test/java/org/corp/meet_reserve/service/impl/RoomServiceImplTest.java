package org.corp.meet_reserve.service.impl;

import org.corp.meet_reserve.api.model.CreateRoomRequest;
import org.corp.meet_reserve.entity.Room;
import org.corp.meet_reserve.exception.RoomAlreadyExistsException;
import org.corp.meet_reserve.repository.RoomRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoomServiceImplTest {

    @Mock
    private RoomRepository roomRepository;

    @InjectMocks
    private RoomServiceImpl roomService;

    @Test
    void createsRoomAndTrimsItsName() {
        CreateRoomRequest request = new CreateRoomRequest("  Большая переговорная  ", 10);
        Room savedRoom = room();
        when(roomRepository.existsByName("Большая переговорная")).thenReturn(false);
        when(roomRepository.saveAndFlush(any(Room.class))).thenReturn(savedRoom);

        var response = roomService.createRoom(request);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("Большая переговорная");
        assertThat(response.getCapacity()).isEqualTo(10);
        assertThat(response.getActive()).isTrue();
        verify(roomRepository).saveAndFlush(any(Room.class));
    }

    @Test
    void rejectsRoomWhenNameAlreadyExists() {
        CreateRoomRequest request = new CreateRoomRequest("Переговорная", 10);
        when(roomRepository.existsByName("Переговорная")).thenReturn(true);

        assertThatThrownBy(() -> roomService.createRoom(request))
                .isInstanceOf(RoomAlreadyExistsException.class);
        verify(roomRepository, never()).saveAndFlush(any(Room.class));
    }

    @Test
    void translatesConcurrentUniqueConstraintViolation() {
        CreateRoomRequest request = new CreateRoomRequest("Переговорная", 10);
        when(roomRepository.existsByName("Переговорная")).thenReturn(false);
        when(roomRepository.saveAndFlush(any(Room.class)))
                .thenThrow(new DataIntegrityViolationException("duplicate room name"));

        assertThatThrownBy(() -> roomService.createRoom(request))
                .isInstanceOf(RoomAlreadyExistsException.class);
    }

    private Room room() {
        Room room = new Room();
        room.setId(1L);
        room.setName("Большая переговорная");
        room.setCapacity(10);
        room.setActive(true);
        room.setCreatedAt(Instant.parse("2026-11-03T10:00:00Z"));
        return room;
    }
}
