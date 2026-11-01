package org.corp.meet_reserve.repository;

import org.corp.meet_reserve.entity.Booking;
import org.corp.meet_reserve.entity.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.Optional;
import jakarta.persistence.LockModeType;

public interface BookingRepository extends JpaRepository<Booking, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT b FROM Booking b WHERE b.id = :id")
    Optional<Booking> findByIdForUpdate(@Param("id") UUID id);

    @Query("""
            SELECT b FROM Booking b
            WHERE b.room.id = :roomId
              AND b.status IN :statuses
              AND b.startTime < :endTime
              AND b.endTime > :startTime
            """)
    List<Booking> findByRoomIdAndStatusInAndStartTimeLessThanAndEndTimeGreaterThan(
            @Param("roomId") Long roomId,
            @Param("statuses") Collection<BookingStatus> statuses,
            @Param("endTime") Instant endTime,
            @Param("startTime") Instant startTime);

    @Modifying(flushAutomatically = true)
    @Query("""
            UPDATE Booking b
            SET b.status = :expiredStatus
            WHERE b.status = :pendingStatus
              AND b.expiresAt <= :now
            """)
    int expireOverdueBookings(
            @Param("now") Instant now,
            @Param("pendingStatus") BookingStatus pendingStatus,
            @Param("expiredStatus") BookingStatus expiredStatus);

}
