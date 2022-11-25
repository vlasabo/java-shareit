package ru.practicum.shareit.item.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


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
        int id = allItems.size() + 1;
        item.setId(id);
        allItems.put(id, item);
        return item;
    }

    @Override
    public List<ItemDto> findAvailableItemDtoByDescOrName(String text) {
        String toFind = text.toLowerCase();
        return allItems.values().stream()
                .filter(x -> (x.getName().toLowerCase().contains(toFind)
                        || x.getDescription().toLowerCase().contains(toFind)))
                .filter(Item::getAvailable)
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

}
