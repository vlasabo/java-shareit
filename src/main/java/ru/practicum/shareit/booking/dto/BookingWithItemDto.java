package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
public class BookingWithItemDto {
    @JsonSerialize
    Integer bookerId;
    @JsonSerialize
    Integer id;

    public BookingWithItemDto(int id, int bookerId) {
        this.id = id;
        this.bookerId = bookerId;
    }
}
