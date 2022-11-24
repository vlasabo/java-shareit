package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    Optional<Item> getItemById(int id);

    List<Item> getAllItems();

    Item save(Item item);

    List<ItemDto> findItemDtoByDescOrName(String text);
}
