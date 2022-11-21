package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum BookingStatus {
    WAITING("новое бронирование"),
    APPROVED("бронирование подтверждено владельцем"),
    REJECTED("бронирование отклонено владельцем"),
    CANCELED("— бронирование отменено создателем");

    private final String desc;

}
