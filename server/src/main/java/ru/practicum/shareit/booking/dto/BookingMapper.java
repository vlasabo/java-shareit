package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.model.Booking;

public class BookingMapper {
    public static BookingWithItemDto toBookingWithItemDto(Booking booking) {
        if (booking == null) {
            return null;
        }
        return new BookingWithItemDto(
                booking.getId(),
                booking.getUserId()
        );
    }
}
