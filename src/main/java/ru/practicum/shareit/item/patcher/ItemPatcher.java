package ru.practicum.shareit.item.patcher;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

public class ItemPatcher {
    public static Item patchItem(Item item, ItemDto itemDto) {
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getItemRequest() != null) {
            item.setItemRequest(itemDto.getItemRequest());
        }
        return item;
    }
}
