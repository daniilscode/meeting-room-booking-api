package org.corp.meet_reserve.service.impl;

import lombok.RequiredArgsConstructor;
import org.corp.meet_reserve.repository.BookingRepository;
import org.corp.meet_reserve.service.CleanupService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

import static org.corp.meet_reserve.entity.BookingStatus.EXPIRED;
import static org.corp.meet_reserve.entity.BookingStatus.PENDING;

@Service
@RequiredArgsConstructor
public class CleanupServiceImpl implements CleanupService {

    private final BookingRepository repository;

    @Override
    @Transactional
    public int cleanupExpiredTimes(Instant time) {
        return repository.expireOverdueBookings(time, PENDING, EXPIRED);
    }
}
