package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.booking.dto.BookingWithItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.ArrayList;
import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ItemDto extends Item {
    private String name;
    private String description;
    private Boolean available;
    private int id;
    private ItemRequest itemRequest;
    BookingWithItemDto lastBooking;
    BookingWithItemDto nextBooking;
    private List<Comment> comments = new ArrayList<>();

    public ItemDto(String name, String description, Boolean available, int id, Integer itemRequestId) {
        this.name = name;
        this.description = description;
        this.available = available;
        this.id = id;
        super.setRequestId(itemRequestId);
    }


}
