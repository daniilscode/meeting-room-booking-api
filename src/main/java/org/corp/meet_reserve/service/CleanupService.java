package org.corp.meet_reserve.service;

import java.time.Instant;

/**
 * Выполняет фоновые операции очистки бронирований.
 */
public interface CleanupService {

    /**
     * Переводит просроченные временные брони в статус EXPIRED.
     */
    int cleanupExpiredTimes(Instant time);
}
