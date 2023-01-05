package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Objects;

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

    @Override
    public int hashCode() {
        int prime = 31;
        return prime * Objects.hash(id, bookerId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookingWithItemDto bookingWithItemDto = (BookingWithItemDto) o;
        return Objects.equals(id, bookingWithItemDto.id) &&
                Objects.equals(bookerId, bookingWithItemDto.bookerId);
    }


}
