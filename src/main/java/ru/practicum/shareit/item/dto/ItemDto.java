package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;

@AllArgsConstructor
@Data
public class ItemDto {
    private String name;
    private String description;
    private boolean available;
    private int id;
    private ItemRequest itemRequest;
}
