package ru.practicum.shareit.item.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@RequiredArgsConstructor
@Repository
public class ItemRepositoryInMemoryImpl implements ItemRepository {
    private final Map<Integer, Item> allItems;

    @Override
    public Optional<Item> getItemById(int id) {
        return Optional.ofNullable(allItems.get(id));
    }

    @Override
    public List<Item> getAllItems() {
        return new ArrayList<>(allItems.values());
    }

    @Override
    public Item save(Item item) {
        int id = (item.getId() == 0) ? allItems.size() + 1 : item.getId();
        item.setId(id);
        allItems.put(id, item);
        return item;
    }
}
