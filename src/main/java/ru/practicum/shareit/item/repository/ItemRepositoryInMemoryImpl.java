package ru.practicum.shareit.item.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Repository
public class ItemRepositoryInMemoryImpl implements ItemRepository {
    private final Map<Integer, Item> allItems;

    @Override
    public Item getItemById(int id) {
        return allItems.getOrDefault(id, null);
    }

    @Override
    public List<Item> getAllItems() {
        return new ArrayList<>(allItems.values());
    }

    @Override
    public void save(Item item) {
        int id = (item.getId() == 0) ? allItems.size() + 1 : item.getId();
        item.setId(id);
        allItems.put(id, item);
    }
}
