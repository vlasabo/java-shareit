package ru.practicum.shareit.item.dto;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.model.Item;

@Service
@NoArgsConstructor
public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getId(),
                item.getItemRequest() != null ? item.getItemRequest() : null
        );
    }

    public static Item toItem(ItemDto itemDto) {
        return new Item(
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                itemDto.getId()
        );
    }

}
