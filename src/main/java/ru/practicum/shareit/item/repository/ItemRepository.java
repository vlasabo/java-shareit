package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    public Item getItemById(int id);

    public List<Item> getAllItems();

    public void save(Item item);
}
