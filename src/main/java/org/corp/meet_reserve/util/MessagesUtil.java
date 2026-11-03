package org.corp.meet_reserve.util;

public interface MessagesUtil {
    String INVALID_END_TIME = "endTime должен быть позже startTime";
    String INVALID_START_TIME = "startTime должен быть в будущем";
    String INVALID_DURATION = "Длительность брони должна быть от 15 минут до 4 часов";
    String ROOM_NOT_FOUND = "Комната не найдена: ";
    String SLOT_ALREADY_BOOKED = "Выбранный интервал уже занят";
    String ROOM_ALREADY_EXISTS = "Комната с таким именем уже существует: ";
    String BOOKING_NOT_PENDING = "Бронь нельзя подтвердить в текущем статусе";
    String BOOKING_NOT_FOUND = "Бронь не найдена: ";
    String BOOKING_EXPIRED = "Срок действия брони истёк";

    String CLEAN_COMPLETE = "Очистка завершена: обновлено записей={}, startedAt={}";
    String CLEAN_ERROR = "Ошибка фоновой очистки, startedAt={}";
}
