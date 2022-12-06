package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingWithItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;


@AllArgsConstructor
@Data
@NoArgsConstructor
public class ItemDto extends Item {
    private String name;
    private String description;
    private Boolean available;
    private int id;
    private ItemRequest itemRequest;
    BookingWithItemDto lastBooking;
    BookingWithItemDto nextBooking;
}
