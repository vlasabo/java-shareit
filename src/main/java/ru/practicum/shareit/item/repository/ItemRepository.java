package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    public Optional<Item> getItemById(int id);

    public List<Item> getAllItems();

    public Item save(Item item);
}
