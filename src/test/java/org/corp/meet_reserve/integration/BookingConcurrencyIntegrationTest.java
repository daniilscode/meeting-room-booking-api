package org.corp.meet_reserve.integration;

import org.corp.meet_reserve.api.model.CreateBookingRequest;
import org.corp.meet_reserve.entity.Room;
import org.corp.meet_reserve.exception.RoomAlreadyBookedException;
import org.corp.meet_reserve.repository.BookingRepository;
import org.corp.meet_reserve.repository.RoomRepository;
import org.corp.meet_reserve.service.BookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
class BookingConcurrencyIntegrationTest {

    @Container
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("meet-service")
            .withUsername("postgres")
            .withPassword("pass");

    @Container
    static final GenericContainer<?> REDIS = new GenericContainer<>("redis:7-alpine")
            .withExposedPorts(6379);

    @Autowired
    private BookingService bookingService;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private RoomRepository roomRepository;

    private Room room;

    @DynamicPropertySource
    static void registerContainerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.data.redis.host", REDIS::getHost);
        registry.add("spring.data.redis.port", () -> REDIS.getMappedPort(6379));
    }

    @BeforeEach
    void setUpDatabase() {
        bookingRepository.deleteAllInBatch();
        roomRepository.deleteAllInBatch();

        room = new Room();
        room.setName("Integration room");
        room.setCapacity(12);
        room.setActive(true);
        room = roomRepository.saveAndFlush(room);
    }

    @Test
    void allowsOnlyOneBookingForTheSameSlotWhenRequestsArriveConcurrently()
            throws Exception {
        int threadCount = 20;
        CountDownLatch ready = new CountDownLatch(threadCount);
        CountDownLatch start = new CountDownLatch(1);
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        List<Future<BookingAttempt>> attempts = new ArrayList<>();

        OffsetDateTime startTime = OffsetDateTime.now(ZoneOffset.UTC)
                .plusHours(1)
                .withSecond(0)
                .withNano(0);
        OffsetDateTime endTime = startTime.plusHours(1);

        try {
            for (int i = 0; i < threadCount; i++) {
                long userId = i + 1L;
                attempts.add(executor.submit(() -> {
                    ready.countDown();
                    assertThat(start.await(10, TimeUnit.SECONDS)).isTrue();

                    try {
                        bookingService.createPendingBooking(
                                new CreateBookingRequest(
                                        room.getId(),
                                        userId,
                                        "Concurrent meeting",
                                        startTime,
                                        endTime));
                        return BookingAttempt.SUCCESS;
                    } catch (RoomAlreadyBookedException exception) {
                        return BookingAttempt.CONFLICT;
                    }
                }));
            }

            assertThat(ready.await(10, TimeUnit.SECONDS)).isTrue();
            start.countDown();

            List<BookingAttempt> results = new ArrayList<>();
            for (Future<BookingAttempt> attempt : attempts) {
                results.add(attempt.get(20, TimeUnit.SECONDS));
            }

            assertThat(results).containsExactlyInAnyOrder(
                    BookingAttempt.SUCCESS,
                    BookingAttempt.CONFLICT,
                    BookingAttempt.CONFLICT,
                    BookingAttempt.CONFLICT,
                    BookingAttempt.CONFLICT,
                    BookingAttempt.CONFLICT,
                    BookingAttempt.CONFLICT,
                    BookingAttempt.CONFLICT,
                    BookingAttempt.CONFLICT,
                    BookingAttempt.CONFLICT,
                    BookingAttempt.CONFLICT,
                    BookingAttempt.CONFLICT,
                    BookingAttempt.CONFLICT,
                    BookingAttempt.CONFLICT,
                    BookingAttempt.CONFLICT,
                    BookingAttempt.CONFLICT,
                    BookingAttempt.CONFLICT,
                    BookingAttempt.CONFLICT,
                    BookingAttempt.CONFLICT,
                    BookingAttempt.CONFLICT);
            assertThat(bookingRepository.count()).isEqualTo(1);
        } finally {
            executor.shutdownNow();
        }
    }

    private enum BookingAttempt {
        SUCCESS,
        CONFLICT
    }
}
