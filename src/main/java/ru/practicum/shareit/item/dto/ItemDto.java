package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;


@AllArgsConstructor
@Data
public class ItemDto extends Item {
    private String name;
    private String description;
    private Boolean available;
    private int id;
    private ItemRequest itemRequest;
}
